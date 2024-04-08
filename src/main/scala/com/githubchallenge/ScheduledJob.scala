package com.githubchallenge

import java.net.URI

import cats.MonadThrow
import cats.data.OptionT
import cats.effect.Async
import cats.effect.Sync
import cats.effect.Temporal
import cats.implicits._
import cron4s.Cron
import cron4s.CronExpr
import cron4s.lib.javatime._
import eu.timepit.fs2cron.Scheduler
import eu.timepit.fs2cron.cron4s.Cron4sScheduler
import io.circe.Decoder
import io.circe.parser.decode
import org.typelevel.log4cats.Logger
import sttp.client3.Identity
import sttp.client3.RequestT
import sttp.client3.Response
import sttp.client3.SttpBackend
import sttp.client3.basicRequest
import sttp.model.Uri

import com.githubchallenge.Config.GithubConfig
import com.githubchallenge.model.CommitDetails
import com.githubchallenge.model.CommitRequest
import com.githubchallenge.model.GithubCommit
import com.githubchallenge.model.GithubPull
import com.githubchallenge.model.PullDetails
import com.githubchallenge.model.Repository
import com.githubchallenge.service.GithubWebhookService
import com.githubchallenge.service.ProjectMetricsService

object ScheduledJob {
  private def cronScheduler[F[_]: Temporal: Sync]: Scheduler[F, CronExpr] =
    Cron4sScheduler.systemDefault[F]

  private def runJob[F[_]: MonadThrow: Logger: Calendar](
      githubWebhookService: GithubWebhookService[F],
      projectMetricsService: ProjectMetricsService[F],
      githubConfig: GithubConfig,
    )(implicit
      backend: SttpBackend[F, Any]
    ): F[Unit] =
    (for {
      _ <- Logger[F].info("ScheduledJob: Starting")
      repos <- projectMetricsService.getProjects
      repoCommits <- repos
        .traverse { repo =>
          fetchCommitsFromGitHub[F](
            githubConfig.token,
            repo.owner.login,
            repo.name,
          ).map { commits =>
            repo -> commits
          }
        }
        .map(_.toMap)

      commitsList = repoCommits.flatMap {
        case repo -> commits =>
          commits
            .groupBy(_.committer)
            .map {
              case (committer, commit) =>
                CommitDetails(
                  commits = commit.map(githubCommit =>
                    CommitRequest(id = githubCommit.sha, githubCommit.commit.message)
                  ),
                  user = committer,
                  repo = repo,
                )
            }
      }.toList

      _ <- githubWebhookService.insertBatchCommits(commitsList)
      pulls <- repos.flatTraverse { repo =>
        fetchPullsFromGitHub[F](
          repo,
          githubConfig.token,
          repo.owner.login,
          repo.name,
        )
      }

      _ <- githubWebhookService.insertBatchPRs(pulls)
      _ <- Logger[F].info("ScheduledJob: Finished")
    } yield ())
      .handleErrorWith { throwable =>
        Logger[F].error(throwable)("Error occurred run job")
      }

  private def fetchCommitsFromGitHub[F[_]: MonadThrow: Logger](
      githubToken: String,
      owner: String,
      repo: String,
    )(implicit
      backend: SttpBackend[F, Any]
    ): F[List[GithubCommit]] = {
    val uri = Uri(URI.create(s"https://api.github.com/repos/$owner/$repo/commits"))
    val request = basicRequest
      .header("Accept", "application/vnd.github+json")
      .header("Authorization", s"Bearer $githubToken")
      .header("X-GitHub-Api-Version", "2022-11-28")
      .get(uri)

    sendRequest[F, GithubCommit](request)
  }

  private def fetchPullsFromGitHub[F[_]: MonadThrow: Logger](
      repository: Repository,
      githubToken: String,
      owner: String,
      repo: String,
    )(implicit
      backend: SttpBackend[F, Any]
    ): F[List[PullDetails]] = {
    val uri = Uri(
      URI.create(s"https://api.github.com/repos/$owner/$repo/pulls")
    )
    val request = basicRequest
      .header("Accept", "application/vnd.github+json")
      .header("Authorization", s"Bearer $githubToken")
      .header("X-GitHub-Api-Version", "2022-11-28")
      .get(uri)

    for {
      pulls <- sendRequest[F, GithubPull](request)
      detailedPulls = pulls.map { pull =>
        PullDetails(
          pull.id,
          pull.state,
          pull.user,
          repository,
        )
      }
    } yield detailedPulls
  }

  private def sendRequest[F[_]: MonadThrow: Logger, A: Decoder](
      request: RequestT[Identity, Either[String, String], Any]
    )(implicit
      backend: SttpBackend[F, Any]
    ): F[List[A]] =
    (for {
      response <- backend.send(request)
      commits <- handleResponse[F, A](response)
    } yield commits)
      .handleErrorWith { error =>
        Logger[F].error(error)("No response").as(Nil)
      }

  private def handleResponse[F[_]: MonadThrow: Logger, A: Decoder](
      response: Response[Either[String, String]]
    ): F[List[A]] =
    response.body match {
      case Left(error) =>
        val errorMsg = s"GitHub request failed with error: $error"
        Logger[F].error(errorMsg) *>
          new Exception(
            s"Error occurred while getting list response body"
          )
            .raiseError[F, List[A]]

      case Right(json) =>
        decode[List[A]](json) match {
          case Left(err) =>
            val errorMsg = s"Failed to parse GitHub response: $err"
            Logger[F].error(errorMsg) *>
              new Exception(
                s"Error occurred while decoding list"
              )
                .raiseError[F, List[A]]

          case Right(commits) => MonadThrow[F].pure(commits)
        }
    }

  def make[F[_]: Async: Calendar: Logger](
      githubWebhookService: GithubWebhookService[F],
      projectMetricsService: ProjectMetricsService[F],
      githubConfig: GithubConfig,
      interval: String,
    )(implicit
      backend: SttpBackend[F, Any]
    ): F[Unit] = for {
    cronExprOpt <- OptionT
      .fromOption[F](Cron.parse(interval).toOption)
      .getOrRaise(new IllegalArgumentException("Incorrect cron Expr entered"))
    now <- Calendar[F].currentDateTime
    nextTime = cronExprOpt.next(now)
    _ <- nextTime.traverse(nextTime => Logger[F].info(s"Job next run $nextTime"))
    _ <- cronScheduler
      .awakeEvery(cronExprOpt)
      .evalTap { _ =>
        for {
          _ <- nextTime.traverse(nextTime => Logger[F].info(s"Job next run $nextTime"))
          _ <- runJob(githubWebhookService, projectMetricsService, githubConfig)
        } yield {}
      }
      .compile
      .drain
  } yield {}
}

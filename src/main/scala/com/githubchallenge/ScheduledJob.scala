package com.githubchallenge

import java.net.URI

import cats.MonadThrow
import cats.implicits._
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
import com.githubchallenge.model.Repository
import com.githubchallenge.service.GithubWebhookService

object ScheduledJob {
  def runJob[F[_]: MonadThrow: Logger: Calendar](
      githubWebhookService: GithubWebhookService[F],
      githubConfig: GithubConfig,
    )(implicit
      backend: SttpBackend[F, Any]
    ): F[Unit] =
    for {
      _ <- Logger[F].info("ScheduledJob: Starting")
      commits <- fetchCommitsFromGitHub[F](
        githubConfig.token,
        githubConfig.owner,
        githubConfig.repo,
      )
      commitsList = commits
        .groupBy(_.committer)
        .map {
          case (committer, commit) =>
            CommitDetails(
              commits =
                commit.map(githubCommit => CommitRequest(id = githubCommit.sha, githubCommit.commit.message)),
              user = committer,
              repo = Repository(id = githubConfig.repoId, name = githubConfig.owner),
            )
        }
        .toList
      _ <- githubWebhookService.insertBatchCommits(commitsList)

      _ <- Logger[F].info("ScheduledJob: Finished")
    } yield ()

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

    sendRequest[F](request)
  }

  private def sendRequest[F[_]: MonadThrow: Logger](
      request: RequestT[Identity, Either[String, String], Any]
    )(implicit
      backend: SttpBackend[F, Any]
    ): F[List[GithubCommit]] =
    for {
      response <- backend.send(request)
      commits <- handleResponse[F](response)
    } yield commits

  private def handleResponse[F[_]: MonadThrow: Logger](
      response: Response[Either[String, String]]
    ): F[List[GithubCommit]] =
    response.body match {
      case Left(error) =>
        val errorMsg = s"GitHub request failed with error: $error"
        Logger[F].error(errorMsg) *>
          new Exception(
            s"Error occurred while getting list commits response body"
          )
            .raiseError[F, List[GithubCommit]]

      case Right(json) =>
        decode[List[GithubCommit]](json) match {
          case Left(err) =>
            val errorMsg = s"Failed to parse GitHub response: $err"
            Logger[F].error(errorMsg) *>
              new Exception(
                s"Error occurred while decoding list commits"
              )
                .raiseError[F, List[GithubCommit]]

          case Right(commits) => MonadThrow[F].pure(commits)
        }
    }
}

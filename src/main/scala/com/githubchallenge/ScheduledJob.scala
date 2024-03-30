package com.githubchallenge

import java.net.URI


import cats.MonadThrow
import cats.implicits._
import io.circe.parser.decode
import org.typelevel.log4cats.Logger
import sttp.client3.HttpURLConnectionBackend
import sttp.client3.Identity
import sttp.client3.RequestT
import sttp.client3.Response
import sttp.client3.SttpBackend
import sttp.client3.basicRequest
import sttp.model.Uri

import com.githubchallenge.model.CommitDetails
import com.githubchallenge.service.GithubWebhookService

object ScheduledJob {
  def runJob[F[_]: MonadThrow: Logger: Calendar](
      githubWebhookService: GithubWebhookService[F],
      githubToken: String, // Your GitHub token
      owner: String, // GitHub repository owner
      repo: String, // GitHub repository name
    ): F[Unit] =
    for {
      _ <- Logger[F].info("ScheduledJob: Starting")
      commits <- fetchCommitsFromGitHub[F](githubToken, owner, repo)
      _ <- githubWebhookService.insertCommits(commits.head)
      _ <- Logger[F].info("ScheduledJob: Finished")
    } yield ()

  private def fetchCommitsFromGitHub[F[_]: MonadThrow: Logger](
      githubToken: String,
      owner: String,
      repo: String,
    ): F[List[CommitDetails]] = {
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
    ): F[List[CommitDetails]] = {
    implicit val backend: SttpBackend[Identity, Any] = HttpURLConnectionBackend()
    for {
      response <- backend.send(request)
      commits <- handleResponse(response)
    } yield commits
  }

  private def handleResponse[F[_]: MonadThrow: Logger](
      response: Response[Either[String, String]]
    ): F[List[CommitDetails]] =
    response.body match {
      case Left(error) =>
        val errorMsg = s"GitHub request failed with error: $error"
        Logger[F].error(errorMsg) *> MonadThrow[F].raiseError(new RuntimeException(errorMsg))
      case Right(json) =>
        decode[List[CommitDetails]](json) match {
          case Left(err) =>
            val errorMsg = s"Failed to parse GitHub response: $err"
            Logger[F].error(errorMsg) *> MonadThrow[F].raiseError(new RuntimeException(errorMsg))
          case Right(commits) => MonadThrow[F].pure(commits)
        }
    }
}

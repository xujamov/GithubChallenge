package com.githubchallenge.api

import cats.Monad
import cats.effect.kernel.Concurrent
import cats.implicits._
import io.circe.Json
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger

import com.githubchallenge.model.CommitDetails
import com.githubchallenge.model.GithubEvent
import com.githubchallenge.model.PullDetails
import com.githubchallenge.service.GithubWebhookService

final case class GithubWebhookApi[F[_]: Monad: Concurrent: JsonDecoder](
    service: GithubWebhookService[F]
  )(implicit
    logger: Logger[F]
  ) extends Http4sDsl[F] {
  lazy val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "webhook" =>
      req.decode[Json] { json =>
        println(json)
        json
          .as[GithubEvent]
          .fold(
            error => BadRequest(s"Invalid JSON: $error"),
            event => {
              println(event)
              (event match {
                case GithubEvent(_, _, Some(pullRequest), _) =>
                  service.insertPRs(
                    PullDetails(pullRequest.id, pullRequest.state, event.sender, event.repository)
                  )
                case GithubEvent(_, _, _, Some(commits)) =>
                  service.insertCommits(CommitDetails(commits, event.sender, event.repository))
                case _ =>
                  Monad[F].unit
              }).flatMap(_ => Ok()).handleErrorWith { e =>
                println(s"Error: $e")
                logger.debug(s"Error: $e") >> BadRequest("Something went wrong")
              }
            },
          )
      }

  }
}

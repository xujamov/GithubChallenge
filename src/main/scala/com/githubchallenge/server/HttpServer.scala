package com.githubchallenge.server

import cats.Monad
import cats.MonadThrow
import cats.data.NonEmptyList
import cats.effect.Async
import cats.effect.Resource
import cats.effect.kernel.Concurrent
import cats.syntax.all._
import com.comcast.ip4s.Host
import com.comcast.ip4s.Port
import org.http4s.HttpRoutes
import org.http4s.circe.JsonDecoder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.typelevel.log4cats.Logger

import com.githubchallenge.api.ContributorMetricsApi
import com.githubchallenge.api.GithubWebhookApi
import com.githubchallenge.api.ProjectMetricsApi
import com.githubchallenge.setup.Environment

object HttpServer {
  private def allRoutes[F[_]: Monad: Concurrent: MonadThrow: JsonDecoder: Logger](
      env: Environment[F]
    ): NonEmptyList[HttpRoutes[F]] =
    NonEmptyList
      .of[HttpRoutes[F]](
        new ContributorMetricsApi[F](env.services.contributorMetricsService).routes,
        new ProjectMetricsApi[F](env.services.projectMetricsService).routes,
        new GithubWebhookApi[F](env.services.githubWebhookService).routes,
      )
  def run[F[_]: Async](
      env: Environment[F]
    )(implicit
      logger: Logger[F]
    ): Resource[F, Unit] =
    EmberServerBuilder
      .default[F]
      .withHostOption(Host.fromString("0.0.0.0"))
      .withPort(
        Port
          .fromInt(env.config.httpServer.port)
          .getOrElse(throw new IllegalArgumentException("Port is incorrect"))
      )
      .withHttpApp(allRoutes[F](env).toList.reduce(_ <+> _).orNotFound)
      .build
      .evalMap(_ => logger.info(s"Github Challenge http server is started"))
}

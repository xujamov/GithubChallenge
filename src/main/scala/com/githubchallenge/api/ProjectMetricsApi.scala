package com.githubchallenge.api

import cats.Monad
import cats.implicits._
import com.githubchallenge.service.ProjectMetricsService
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger

final case class ProjectMetricsApi[F[_]: Monad: JsonDecoder](
    service: ProjectMetricsService[F]
  )(implicit
    logger: Logger[F]
  ) extends Http4sDsl[F] {

  lazy val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "projects" / projectId / "metrics" =>
      service.getProjectMetrics(projectId.toLong).flatMap(Ok(_))

  }
}

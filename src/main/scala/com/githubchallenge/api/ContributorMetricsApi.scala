package com.githubchallenge.api

import cats.Monad
import cats.implicits._
import com.githubchallenge.service.ContributorMetricsService
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger

final case class ContributorMetricsApi[F[_]: Monad: JsonDecoder](
    service: ContributorMetricsService[F]
  )(implicit
    logger: Logger[F]
  ) extends Http4sDsl[F] {
  lazy val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "contributors" / contributorId / "metrics" =>
      service.getContributorMetrics(contributorId.toLong).flatMap(Ok(_))
  }
}

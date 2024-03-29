package com.githubchallenge.service

import cats.effect.Async
import org.typelevel.log4cats.Logger

import com.githubchallenge.db.Repositories

case class Services[F[_]](
    contributorMetricsService: ContributorMetricsService[F],
    projectMetricsService: ProjectMetricsService[F],
    githubWebhookService: GithubWebhookService[F],
  )

object Services {
  def make[F[_]: Async: Logger](
      repositories: Repositories[F]
    ): Services[F] =
    Services[F](
      contributorMetricsService = ContributorMetricsService.make[F](repositories.contributors),
      projectMetricsService = ProjectMetricsService.make[F](repositories.projects),
      githubWebhookService = GithubWebhookService
        .make[F](repositories.githubWebhook, repositories.projects, repositories.contributors),
    )
}

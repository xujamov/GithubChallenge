package com.githubchallenge.db

import cats.effect.Async
import com.githubchallenge.db.repositories.ContributorsRepository
import com.githubchallenge.db.repositories.GithubWebhookRepository
import com.githubchallenge.db.repositories.ProjectsRepository
import doobie.util.transactor.Transactor

case class Repositories[F[_]](
    contributors: ContributorsRepository[F],
    projects: ProjectsRepository[F],
    githubWebhook: GithubWebhookRepository[F],
  )

object Repositories {
  def make[F[_]: Async](implicit transactor: Transactor[F]): Repositories[F] =
    Repositories(
      contributors = ContributorsRepository.make[F](transactor),
      projects = ProjectsRepository.make[F](transactor),
      githubWebhook = GithubWebhookRepository.make[F](transactor),
    )
}

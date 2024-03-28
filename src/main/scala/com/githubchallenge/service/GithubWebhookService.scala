package com.githubchallenge.service

import cats.Monad
import cats.implicits.toFlatMapOps
import cats.implicits.toFunctorOps
import com.githubchallenge.db.repositories.ContributorsRepository
import com.githubchallenge.db.repositories.GithubWebhookRepository
import com.githubchallenge.db.repositories.ProjectsRepository
import com.githubchallenge.model.PullRequest

trait GithubWebhookService[F[_]] {
  def insertPRs(event: PullRequest): F[Unit]
//  def insertCommit(event: Commit): F[Unit]
}

object GithubWebhookService {
  def make[F[_]: Monad](
      githubWebhookRepository: GithubWebhookRepository[F],
      projectsRepository: ProjectsRepository[F],
      contributorsRepository: ContributorsRepository[F],
    ): GithubWebhookService[F] =
    new GithubWebhookService[F] {
      override def insertPRs(event: PullRequest): F[Unit] =
        for {
          _ <- projectsRepository.insert(event.base.repo)
          _ <- contributorsRepository.insert(event.base.user)
          _ <- githubWebhookRepository.create(event)
        } yield ()
//      override def insertCommit(event: Commit): F[Unit] = ???

    }
}

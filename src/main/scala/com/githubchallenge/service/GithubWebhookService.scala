package com.githubchallenge.service

import cats.Monad
import cats.data.NonEmptyList
import cats.implicits.toFlatMapOps
import cats.implicits.toFoldableOps
import cats.implicits.toFunctorOps
import com.githubchallenge.db.repositories.ContributorsRepository
import com.githubchallenge.db.repositories.GithubWebhookRepository
import com.githubchallenge.db.repositories.ProjectsRepository
import com.githubchallenge.db.repositories.sql.Dto
import com.githubchallenge.model.CommitDetails
import com.githubchallenge.model.PullDetails

trait GithubWebhookService[F[_]] {
  def insertPRs(event: PullDetails): F[Unit]
  def insertCommits(event: CommitDetails): F[Unit]
}

object GithubWebhookService {
  def make[F[_]: Monad](
      githubWebhookRepository: GithubWebhookRepository[F],
      projectsRepository: ProjectsRepository[F],
      contributorsRepository: ContributorsRepository[F],
    ): GithubWebhookService[F] =
    new GithubWebhookService[F] {
      override def insertPRs(event: PullDetails): F[Unit] =
        for {
          _ <- projectsRepository.insert(event.repo)
          _ <- contributorsRepository.insert(event.user)
          _ <- githubWebhookRepository.createPullRequest(event)
        } yield ()

      override def insertCommits(event: CommitDetails): F[Unit] =
        for {
          _ <- projectsRepository.insert(event.repo)
          _ <- contributorsRepository.insert(event.user)
          _ <- NonEmptyList.fromList(event.commits).traverse_ { commits =>
            githubWebhookRepository.createBatch(
              commits
                .map(commit => Dto.Commit(commit.id, commit.message, event.repo.id, event.user.id))
            )
          }
        } yield ()
    }
}

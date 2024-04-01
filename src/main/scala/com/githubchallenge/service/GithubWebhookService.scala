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
  def insertBatchPRs(event: List[PullDetails]): F[Unit]
  def insertCommits(event: CommitDetails): F[Unit]
  def insertBatchCommits(event: List[CommitDetails]): F[Unit]
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

      override def insertBatchPRs(
          pulls: List[PullDetails]
        ): F[Unit] = {
        NonEmptyList.fromList(pulls).traverse_ { commits =>
          githubWebhookRepository.createBatchPulls(commits)
        }
      }

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
      override def insertBatchCommits(
          commits: List[CommitDetails]
        ): F[Unit] = {
        val commitDetailsList = commits
          .flatMap(commitDetails =>
            commitDetails
              .commits
              .map(commit =>
                Dto.Commit(
                  commit.id,
                  commit.message,
                  commitDetails.repo.id,
                  commitDetails.user.id,
                )
              )
          )
        for {
          _ <- NonEmptyList.fromList(commits.map(_.repo)).traverse_ { repos =>
            projectsRepository.insertBatch(repos)
          }
          _ <- NonEmptyList.fromList(commits.map(_.user)).traverse_ { users =>
            contributorsRepository.insertBatch(users)
          }
          _ <- NonEmptyList.fromList(commitDetailsList).traverse_ { commits =>
            githubWebhookRepository.createBatch(commits)
          }
        } yield ()
      }
    }
}

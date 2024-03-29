package com.githubchallenge.db.repositories

import cats.data.NonEmptyList
import cats.effect.kernel.MonadCancelThrow
import doobie.implicits._
import doobie.util.transactor.Transactor

import com.githubchallenge.db.repositories.sql.Dto.Commit
import com.githubchallenge.db.repositories.sql.GithubWebhookSql
import com.githubchallenge.model.PullDetails

trait GithubWebhookRepository[F[_]] {
  def createPullRequest(event: PullDetails): F[Unit]
  def createBatch(commits: NonEmptyList[Commit]): F[Unit]
}

object GithubWebhookRepository {
  def make[F[_]: MonadCancelThrow](transactor: Transactor[F]): GithubWebhookRepository[F] =
    new GithubWebhookRepository[F] {
      override def createBatch(commits: NonEmptyList[Commit]): F[Unit] = {
        val commitsList = commits.toList
        GithubWebhookSql.createCommit(commitsList).transact(transactor)
      }

      override def createPullRequest(pull: PullDetails): F[Unit] =
        GithubWebhookSql.createPullRequest(pull).transact(transactor)
    }
}

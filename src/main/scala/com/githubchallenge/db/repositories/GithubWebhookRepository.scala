package com.githubchallenge.db.repositories

import cats.effect.kernel.MonadCancelThrow
import com.githubchallenge.db.repositories.sql.GithubWebhookSql
import com.githubchallenge.model.PullRequest
import doobie.implicits._
import doobie.util.transactor.Transactor

trait GithubWebhookRepository[F[_]] {
  def create(event: PullRequest): F[Unit]
}

object GithubWebhookRepository {
  def make[F[_]: MonadCancelThrow](transactor: Transactor[F]): GithubWebhookRepository[F] =
    new GithubWebhookRepository[F] {
      override def create(event: PullRequest): F[Unit] =
        GithubWebhookSql.create(event).transact(transactor)
    }
}

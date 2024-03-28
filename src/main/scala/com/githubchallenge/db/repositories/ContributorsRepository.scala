package com.githubchallenge.db.repositories

import cats.effect.kernel.MonadCancelThrow
import com.githubchallenge.db.repositories.sql.ContributorsSql
import com.githubchallenge.model.Contributor
import com.githubchallenge.model.ContributorMetrics
import doobie.implicits._
import doobie.util.transactor.Transactor

trait ContributorsRepository[F[_]] {
  def insert(contributor: Contributor): F[Unit]
  def findById(id: Long): F[Option[ContributorMetrics]]
}

object ContributorsRepository {
  def make[F[_]: MonadCancelThrow](transactor: Transactor[F]): ContributorsRepository[F] =
    new ContributorsRepository[F] {
      override def insert(contributor: Contributor): F[Unit] =
        ContributorsSql.insert(contributor).transact(transactor)

      override def findById(id: Long): F[Option[ContributorMetrics]] =
        ContributorsSql.selectById(id).transact(transactor)
    }
}

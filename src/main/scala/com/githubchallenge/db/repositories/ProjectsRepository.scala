package com.githubchallenge.db.repositories


import cats.effect.kernel.MonadCancelThrow
import com.githubchallenge.db.repositories.sql.ProjectsSql
import com.githubchallenge.model.ProjectMetrics
import com.githubchallenge.model.Repository
import doobie.implicits._
import doobie.util.transactor.Transactor

trait ProjectsRepository[F[_]] {
  def insert(project: Repository): F[Unit]
  def findById(id: Long): F[Option[ProjectMetrics]]
}

object ProjectsRepository {
  def make[F[_]: MonadCancelThrow](transactor: Transactor[F]): ProjectsRepository[F] =
    new ProjectsRepository[F] {
      override def insert(project: Repository): F[Unit] =
        ProjectsSql.insert(project).transact(transactor)

      override def findById(id: Long): F[Option[ProjectMetrics]] =
        ProjectsSql.selectById(id).transact(transactor)
    }
}

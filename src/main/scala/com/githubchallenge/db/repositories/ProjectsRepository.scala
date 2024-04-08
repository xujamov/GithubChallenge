package com.githubchallenge.db.repositories

import cats.data.NonEmptyList
import cats.effect.kernel.MonadCancelThrow
import doobie.implicits._
import doobie.util.transactor.Transactor

import com.githubchallenge.db.repositories.sql.ProjectsSql
import com.githubchallenge.model.ProjectMetrics
import com.githubchallenge.model.Repository

trait ProjectsRepository[F[_]] {
  def getProjects: F[List[Repository]]
  def insert(project: Repository): F[Unit]
  def insertBatch(projects: NonEmptyList[Repository]): F[Unit]
  def findById(id: Long): F[Option[ProjectMetrics]]
}

object ProjectsRepository {
  def make[F[_]: MonadCancelThrow](transactor: Transactor[F]): ProjectsRepository[F] =
    new ProjectsRepository[F] {
      override def insert(project: Repository): F[Unit] =
        ProjectsSql.insert(project.toRepositoryWithOwnerId).transact(transactor)

      override def insertBatch(projects: NonEmptyList[Repository]): F[Unit] = {
        val projectsList = projects.toList.map(_.toRepositoryWithOwnerId)
        ProjectsSql.insertBatch(projectsList).transact(transactor)
      }

      override def findById(id: Long): F[Option[ProjectMetrics]] =
        ProjectsSql.selectById(id).transact(transactor)

      override def getProjects: F[List[Repository]] =
        ProjectsSql.getProjects.transact(transactor)
    }
}

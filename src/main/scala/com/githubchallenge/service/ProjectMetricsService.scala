package com.githubchallenge.service

import cats.Monad

import com.githubchallenge.db.repositories.ProjectsRepository
import com.githubchallenge.model.ProjectMetrics
import com.githubchallenge.model.Repository

trait ProjectMetricsService[F[_]] {
  def getProjects: F[List[Repository]]
  def getProjectMetrics(projectId: Long): F[Option[ProjectMetrics]]
}

object ProjectMetricsService {
  def make[F[_]: Monad](
      projectRepository: ProjectsRepository[F]
    ): ProjectMetricsService[F] =
    new ProjectMetricsService[F] {
      override def getProjectMetrics(projectId: Long): F[Option[ProjectMetrics]] =
        projectRepository.findById(projectId)
      override def getProjects: F[List[Repository]] =
        projectRepository.getProjects
    }
}

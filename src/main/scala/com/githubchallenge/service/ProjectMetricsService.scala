package com.githubchallenge.service

import cats.Monad

import com.githubchallenge.db.repositories.ProjectsRepository
import com.githubchallenge.model.ProjectMetrics

trait ProjectMetricsService[F[_]] {
  def getProjectMetrics(projectId: Long): F[Option[ProjectMetrics]]
}

object ProjectMetricsService {
  def make[F[_]: Monad](
      projectRepository: ProjectsRepository[F]
    ): ProjectMetricsService[F] =
    new ProjectMetricsService[F] {
      override def getProjectMetrics(projectId: Long): F[Option[ProjectMetrics]] =
        projectRepository.findById(projectId)
    }
}

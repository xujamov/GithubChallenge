package com.githubchallenge.service

import cats.Monad

import com.githubchallenge.db.repositories.ContributorsRepository
import com.githubchallenge.model.ContributorMetrics

trait ContributorMetricsService[F[_]] {
  def getContributorMetrics(contributorId: Long): F[Option[ContributorMetrics]]
}

object ContributorMetricsService {
//  def apply[F[_]: Sync]: ContributorMetricsService[F] = new ContributorMetricsServiceImpl[F]
  def make[F[_]: Monad](
      contributorRepository: ContributorsRepository[F]
    ): ContributorMetricsService[F] =
    new ContributorMetricsService[F] {
      override def getContributorMetrics(contributorId: Long): F[Option[ContributorMetrics]] =
        contributorRepository.findById(contributorId)
    }
}
//class ContributorMetricsServiceImpl[F[_]: Sync] extends ContributorMetricsService[F] {
//  override def getContributorMetrics(contributorId: UUID): F[ContributorMetrics] = ???
//}

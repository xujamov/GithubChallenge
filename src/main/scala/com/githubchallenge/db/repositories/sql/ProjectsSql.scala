package com.githubchallenge.db.repositories.sql

import cats.implicits.toFunctorOps
import doobie.implicits._
import doobie.util.update.Update

import com.githubchallenge.db.repositories.sql.Dto.RepositoryWithOwnerId
import com.githubchallenge.model.ProjectMetrics
import com.githubchallenge.model.Repository

private[repositories] object ProjectsSql {
  def insert(project: RepositoryWithOwnerId): doobie.ConnectionIO[Unit] =
    fr"""
        INSERT INTO projects
        VALUES (?, ?, ?)
        ON CONFLICT (id) DO NOTHING;
      """.update.run.void

  def insertBatch(projects: List[RepositoryWithOwnerId]): doobie.ConnectionIO[Unit] = {
    val fragment = s"""
        INSERT INTO projects
        VALUES (?, ?, ?)
        ON CONFLICT (id) DO NOTHING;
      """

    Update[RepositoryWithOwnerId](fragment).updateMany[List](projects).void
  }

  def getProjects: doobie.ConnectionIO[List[Repository]] =
    fr"""
      SELECT p.id, p.name, c.id, c.login
      FROM projects p
      JOIN contributors c ON c.id = p.owner_id
    """.query[Dto.Repository].to[List].map(_.map(_.toDomain))

  def selectById(id: Long): doobie.ConnectionIO[Option[ProjectMetrics]] =
    fr"""
    SELECT
      COUNT(DISTINCT cm.contributor_id) AS total_contributors,
      COUNT(DISTINCT cm.id) AS total_commits,
      COUNT(DISTINCT CASE WHEN pr.is_closed = true THEN pr.id END) AS total_closed_prs,
      COUNT(DISTINCT CASE WHEN pr.is_closed = false THEN pr.id END) AS total_open_prs
    FROM
      projects p
    LEFT JOIN
      pull_requests pr ON p.id = pr.project_id
    LEFT JOIN
      commits cm ON p.id = cm.project_id
    WHERE
      p.id = $id
  """.query[ProjectMetrics].option
}

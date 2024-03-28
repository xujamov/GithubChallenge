package com.githubchallenge.db.repositories.sql

import cats.implicits.toFunctorOps
import com.githubchallenge.model.ProjectMetrics
import com.githubchallenge.model.Repository
import doobie.implicits._

private[repositories] object ProjectsSql {
  def insert(project: Repository): doobie.ConnectionIO[Unit] =
    fr"""
        INSERT INTO projects
        VALUES (${project.id}, ${project.name})
        ON CONFLICT (id) DO NOTHING;
      """.update.run.void

  def selectById(id: Long): doobie.ConnectionIO[Option[ProjectMetrics]] =
    fr"""
    SELECT
      COUNT(DISTINCT pc.contributor_id) AS total_contributors,
      COUNT(p.id) AS total_commits,
      COUNT(pr.id) AS total_closed_prs,
      COUNT(CASE WHEN pr.is_closed = FALSE THEN 1 END) AS total_open_prs
    FROM
      projects p
    LEFT JOIN
      project_contributors pc ON p.id = pc.project_id
    LEFT JOIN
      pull_requests pr ON p.id = pr.project_id
    WHERE
      p.id = $id
  """.query[ProjectMetrics].option
}

package com.githubchallenge.db.repositories.sql

import cats.implicits.toFunctorOps
import com.githubchallenge.model.Contributor
import com.githubchallenge.model.ContributorMetrics
import doobie.implicits._

private[repositories] object ContributorsSql {
  def insert(contributor: Contributor): doobie.ConnectionIO[Unit] =
    fr"""
        INSERT INTO contributors
        VALUES (${contributor.id}, ${contributor.login})
        ON CONFLICT (id) DO NOTHING;
      """.update.run.void

  def selectById(id: Long): doobie.ConnectionIO[Option[ContributorMetrics]] =
    fr"""
    SELECT
      COUNT(DISTINCT pc.project_id) AS total_projects,
      COUNT(p.id) AS total_commits,
      COUNT(pr.id) AS total_closed_prs,
      COUNT(CASE WHEN pr.is_closed = FALSE THEN 1 END) AS total_open_prs
    FROM
        contributors c
    LEFT JOIN
        project_contributors pc ON c.id = pc.contributor_id
    LEFT JOIN
        projects p ON pc.project_id = p.id
    LEFT JOIN
        pull_requests pr ON c.id = pr.contributor_id
    WHERE
      c.id = $id
  """.query[ContributorMetrics].option
}

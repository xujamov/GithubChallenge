package com.githubchallenge.db.repositories.sql

import cats.implicits.toFunctorOps
import com.githubchallenge.model.Contributor
import com.githubchallenge.model.ContributorMetrics
import doobie.implicits._
import doobie.util.update.Update

private[repositories] object ContributorsSql {
  def insert(contributor: Contributor): doobie.ConnectionIO[Unit] =
    fr"""
        INSERT INTO contributors
        VALUES (${contributor.id}, ${contributor.login})
        ON CONFLICT (id) DO NOTHING;
      """.update.run.void

  def insertBatch(contributors: List[Contributor]): doobie.ConnectionIO[Unit] = {
    val fragment = s"""
        INSERT INTO projects
        VALUES (?, ?)
        ON CONFLICT (id) DO NOTHING;
      """

    Update[Contributor](fragment).updateMany[List](contributors).void
  }

  def selectById(id: Long): doobie.ConnectionIO[Option[ContributorMetrics]] =
    fr"""
      SELECT
        COUNT(DISTINCT cm.project_id) AS total_projects,
        COUNT(cm.id) AS total_commits,
        COUNT(DISTINCT CASE WHEN pr.is_closed = true THEN pr.id END) AS total_closed_prs,
        COUNT(DISTINCT CASE WHEN pr.is_closed = false THEN pr.id END) AS total_open_prs
      FROM
        contributors c
      LEFT JOIN
        commits cm ON c.id = cm.contributor_id
      LEFT JOIN
        pull_requests pr ON c.id = pr.contributor_id
      WHERE
        c.id = $id
    """.query[ContributorMetrics].option
}

package com.githubchallenge.db.repositories.sql

import cats.implicits.toFunctorOps
import doobie.implicits._
import doobie.util.update.Update

import com.githubchallenge.db.repositories.sql.Dto.Commit
import com.githubchallenge.db.repositories.sql.Dto.Pull
import com.githubchallenge.model.PullDetails

private[repositories] object GithubWebhookSql {
  def createPullRequest(event: PullDetails): doobie.ConnectionIO[Unit] = {
    val isClosed: Boolean = event.state != "open"
    fr"""
      INSERT INTO pull_requests
      VALUES (${event.id}, ${event.repo.id}, ${event.user.id}, $isClosed)
      ON CONFLICT (id)
      DO UPDATE
      SET is_closed = $isClosed;
    """.update.run.void
  }

  def createBatchPulls(event: List[Pull]): doobie.ConnectionIO[Unit] = {
    val fragment = s"""
      INSERT INTO pull_requests
      VALUES (?, ?, ?, ?)
      ON CONFLICT (id) DO NOTHING;
    """

    Update[Pull](fragment).updateMany[List](event).void
  }

  def createCommit(event: List[Commit]): doobie.ConnectionIO[Unit] = {
    val fragment = s"""
      INSERT INTO commits
      VALUES (?, ?, ?, ?)
      ON CONFLICT (id) DO NOTHING;
    """

    Update[Commit](fragment).updateMany[List](event).void
  }
}

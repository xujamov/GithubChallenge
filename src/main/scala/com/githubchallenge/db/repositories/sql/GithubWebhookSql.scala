package com.githubchallenge.db.repositories.sql


import cats.implicits.toFunctorOps
import com.githubchallenge.db.repositories.sql.Dto.Commit
import com.githubchallenge.model.PullDetails
import doobie.implicits._
import doobie.util.update.Update

private[repositories] object GithubWebhookSql {
  def createPullRequest(event: PullDetails): doobie.ConnectionIO[Unit] = {
    val is_closed: Boolean =
      if (event.state == "open") false else true
    fr"""
      INSERT INTO pull_requests
      VALUES (${event.id}, ${event.repo.id}, ${event.user.id}, $is_closed)
      ON CONFLICT (id) DO NOTHING;
    """.update.run.void
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

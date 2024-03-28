package com.githubchallenge.db.repositories.sql

import java.util.UUID

import cats.implicits.toFunctorOps
import com.githubchallenge.model.PullRequest
import doobie.implicits._
import doobie.postgres.implicits._

private[repositories] object GithubWebhookSql {
  def create(event: PullRequest): doobie.ConnectionIO[Unit] = {
    val id: UUID = UUID.randomUUID()
    val is_closed: Boolean =
      if (event.state == "open") false else true
    fr"""
      INSERT INTO pull_requests
      VALUES ($id, ${event.base.repo.id}, ${event.base.user.id}, $is_closed);
    """.update.run.void
  }
}

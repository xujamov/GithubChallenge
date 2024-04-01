package com.githubchallenge.db.repositories.sql

object Dto {
  case class Commit(
      id: String,
      message: String,
      projectId: Long,
      contributorId: Long,
    )

  case class Pull(
      id: Long,
      projectId: Long,
      contributorId: Long,
      isClosed: Boolean,
    )
}

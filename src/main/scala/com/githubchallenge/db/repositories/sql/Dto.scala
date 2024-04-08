package com.githubchallenge.db.repositories.sql

import com.githubchallenge.model
import com.githubchallenge.model.Contributor

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

  case class Repository(
      id: Long,
      name: String,
      ownerId: Long,
      ownerName: String,
    ) {
    def toDomain: model.Repository =
      model
        .Repository(
          id = id,
          name = name,
          owner = Contributor(ownerId, ownerName),
        )
  }

  case class RepositoryWithOwnerId(id: Long, name: String, ownerId: Long)
}

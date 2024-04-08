package com.githubchallenge.model

import com.githubchallenge.db.repositories.sql.Dto.RepositoryWithOwnerId
import io.circe.generic.JsonCodec

@JsonCodec
case class Repository(id: Long, name: String, owner: Contributor){
  def toRepositoryWithOwnerId: RepositoryWithOwnerId = RepositoryWithOwnerId (
    id = id,
    name = name,
    ownerId = owner.id
  )
}

package com.githubchallenge.model

import io.circe.generic.JsonCodec

@JsonCodec
case class PullDetails(
    id: Long,
    state: String,
    user: Contributor,
    repo: Repository,
  )

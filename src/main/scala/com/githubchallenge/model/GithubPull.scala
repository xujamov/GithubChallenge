package com.githubchallenge.model

import io.circe.generic.JsonCodec

@JsonCodec
case class GithubPull(
    id: Long,
    state: String,
    user: Contributor,
  )

package com.githubchallenge.model

import io.circe.generic.JsonCodec

@JsonCodec
case class PullRequest(
    id: Long,
    state: String,
  )

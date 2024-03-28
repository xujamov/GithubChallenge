package com.githubchallenge.model

import io.circe.generic.JsonCodec

@JsonCodec
case class BasePullRequest(
    user: Contributor,
    repo: Repository,
  )

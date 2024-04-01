package com.githubchallenge.model

import io.circe.generic.JsonCodec

@JsonCodec
case class GithubCommitMessage(
    message: String
  )

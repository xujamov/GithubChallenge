package com.githubchallenge.model

import io.circe.generic.JsonCodec

@JsonCodec
case class CommitRequest(id: String, message: String)

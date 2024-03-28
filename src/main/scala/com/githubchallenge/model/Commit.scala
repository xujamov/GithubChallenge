package com.githubchallenge.model

import io.circe.generic.JsonCodec

@JsonCodec
case class Commit(id: String, message: String)

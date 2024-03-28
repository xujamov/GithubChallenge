package com.githubchallenge.model

import io.circe.generic.JsonCodec

@JsonCodec
case class Repository(id: Long, name: String)

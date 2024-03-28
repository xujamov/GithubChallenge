package com.githubchallenge.model

import io.circe.generic.JsonCodec

@JsonCodec
case class Contributor(id: Long, login: String)

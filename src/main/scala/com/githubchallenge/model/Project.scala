package com.githubchallenge.model

import java.util.UUID

import io.circe.generic.JsonCodec

@JsonCodec
case class Project(id: UUID, name: String)

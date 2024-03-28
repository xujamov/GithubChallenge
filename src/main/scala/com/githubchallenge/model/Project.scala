package com.githubchallenge.model

import io.circe.generic.JsonCodec

import java.util.UUID

@JsonCodec
case class Project(id: UUID, name: String)

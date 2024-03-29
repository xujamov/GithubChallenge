package com.githubchallenge.model

import io.circe.generic.JsonCodec

@JsonCodec
case class CommitDetails(
    commits: List[CommitRequest],
    user: Contributor,
    repo: Repository,
  )

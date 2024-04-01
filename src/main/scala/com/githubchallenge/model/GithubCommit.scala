package com.githubchallenge.model

import io.circe.generic.JsonCodec

@JsonCodec
case class GithubCommit(
    sha: String,
    committer: Contributor,
    commit: GithubCommitMessage,
  )

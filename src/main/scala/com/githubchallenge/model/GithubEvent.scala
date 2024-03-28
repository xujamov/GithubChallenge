package com.githubchallenge.model

import io.circe.generic.extras.Configuration
import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec
case class GithubEvent(
    action: Option[String],
    repository: Repository,
    sender: Contributor,
    pullRequest: Option[PullRequest],
    commits: Option[Commit],
  )

object GithubEvent {
  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames
}

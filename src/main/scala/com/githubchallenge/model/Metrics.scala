package com.githubchallenge.model

import io.circe.generic.JsonCodec

@JsonCodec
case class ProjectMetrics(
    totalContributors: Int,
    totalCommits: Int,
    totalClosedPRs: Int,
    totalOpenPRs: Int,
  )

@JsonCodec
case class ContributorMetrics(
    totalProjects: Int,
    totalCommits: Int,
    totalClosedPRs: Int,
    totalOpenPRs: Int,
  )

package com.githubchallenge

import com.githubchallenge.utils.Migrations.MigrationsConfig

case class Config(
    httpServer: Config.HttpServerConfig,
    database: Config.DataBaseConfig,
    github: Config.GithubConfig,
  ) {
  lazy val migrations: MigrationsConfig = MigrationsConfig(
    hostname = database.host,
    port = database.port,
    database = database.database,
    username = database.user,
    password = database.password,
    schema = "public",
    location = "db/migration",
  )
}
object Config {
  case class DataBaseConfig(
      host: String,
      port: Int,
      user: String,
      password: String,
      database: String,
    )
  final case class HttpServerConfig(port: Int)
  case class GithubConfig(
      token: String,
      owner: String,
      repo: String,
      repoId: Long,
    )
}

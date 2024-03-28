package com.githubchallenge

import com.githubchallenge.utils.Migrations.MigrationsConfig

case class Config(
    httpServer: Config.HttpServerConfig,
    database: Config.DataBaseConfig,
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
}

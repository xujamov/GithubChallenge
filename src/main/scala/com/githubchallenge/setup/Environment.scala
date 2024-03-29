package com.githubchallenge.setup

import scala.annotation.unused

import cats.MonadThrow
import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Console
import doobie.util.transactor.Transactor
import org.typelevel.log4cats.Logger
import pureconfig.generic.auto.exportReader

import com.githubchallenge.Config
import com.githubchallenge.ConfigLoader
import com.githubchallenge.db.Repositories
import com.githubchallenge.service.Services
import com.githubchallenge.utils.Migrations

case class Environment[F[_]: Async: MonadThrow: Logger](
    config: Config,
    repositories: Repositories[F],
    services: Services[F],
  ) {}

object Environment {
  @unused
  private def makeTransactor[F[_]: Async](dbConfig: Config.DataBaseConfig): Transactor[F] =
    Transactor.fromDriverManager[F](
      "org.postgresql.Driver",
      s"jdbc:postgresql://${dbConfig.host}:${dbConfig.port}/${dbConfig.database}",
      s"${dbConfig.user}",
      s"${dbConfig.password}",
    )

  def make[F[_]: Async: Console: Logger]: Resource[F, Environment[F]] =
    for {
      config <- Resource.eval(ConfigLoader.load[F, Config])
      _ <- Resource.eval(Migrations.run[F](config.migrations))
      implicit0(transactor: Transactor[F]) = makeTransactor(config.database)
      repositories = Repositories.make[F]
      services = Services
        .make[F](repositories)
    } yield Environment[F](config, repositories, services)
}

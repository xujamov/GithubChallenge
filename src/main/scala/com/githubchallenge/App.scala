package com.githubchallenge

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import com.githubchallenge.server.HttpServer
import com.githubchallenge.setup.Environment

object App extends IOApp {
  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  override def run(
      args: List[String]
    ): IO[ExitCode] = {
    val runnable = for {
      env <- Environment.make[IO]
      _ <- HttpServer.run[IO](env)
    } yield {}
    runnable.useForever
  }
}

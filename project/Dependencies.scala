import sbt._

object Dependencies {
  object Versions {
    lazy val circe = "0.14.3"
    lazy val http4s = "0.23.10"
    lazy val cats = "2.9.0"
    lazy val `cats-effect` = "3.4.8"
    lazy val logback = "1.4.7"
    lazy val log4cats = "2.5.0"
    lazy val postgresql = "42.5.4"
  }
  trait LibGroup {
    def all: Seq[ModuleID]
  }

  object io {
    object circe extends LibGroup {
      private def circe(artifact: String): ModuleID =
        "io.circe" %% s"circe-$artifact" % Versions.circe

      lazy val core: ModuleID = circe("core")
      lazy val generic: ModuleID = circe("generic")
      lazy val parser: ModuleID = circe("parser")
      lazy val `generic-extras`: ModuleID = circe("generic-extras")
      override def all: Seq[ModuleID] = Seq(core, generic, parser, `generic-extras`)
    }
  }
  object org {
    lazy val postgresql: ModuleID = "org.postgresql" % "postgresql" % Versions.postgresql
    object typelevel {
      object cats {
        lazy val core = "org.typelevel"           %% "cats-core"           % Versions.cats
        lazy val effect = "org.typelevel"         %% "cats-effect"         % Versions.`cats-effect`
      }
    }
    object http4s extends LibGroup {
      private def http4s(artifact: String): ModuleID =
        "org.http4s" %% s"http4s-$artifact" % Versions.http4s

      lazy val dsl = http4s("dsl")
      lazy val server = http4s("ember-server")
      lazy val client = http4s("ember-client")
      lazy val circe = http4s("circe")
      lazy val `blaze-server` = http4s("blaze-server")
      override def all: Seq[ModuleID] = Seq(dsl, server, client, circe)
    }
  }

  object ch {
    object qos {
      lazy val logback = "ch.qos.logback" % "logback-classic" % Versions.logback
    }
  }

}

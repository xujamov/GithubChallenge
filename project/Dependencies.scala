import sbt.*

object Dependencies {
  object Versions {
    lazy val circe = "0.14.3"
    lazy val http4s = "0.23.10"
    lazy val cats = "2.10.0"
    lazy val `cats-effect` = "3.4.8"
    lazy val logback = "1.4.14"
    lazy val log4cats = "2.5.0"
    lazy val pureconfig = "0.17.5"
    lazy val doobie = "1.0.0-M4"
    lazy val flyway = "9.16.0"
    lazy val sttp = "3.7.2"
    lazy val cron4s = "0.6.1"
    lazy val `fs2-cron4s` = "0.8.3"
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
  object com {
    object github {
      lazy val cron4s: ModuleID =
        "com.github.alonsodomin.cron4s" %% "cron4s-core" % Versions.cron4s
      object pureconfig extends LibGroup {
        lazy val core: ModuleID =
          "com.github.pureconfig" %% "pureconfig" % Versions.pureconfig
        lazy val enumeratum: ModuleID =
          "com.github.pureconfig" %% "pureconfig-enumeratum" % Versions.pureconfig
        override def all: Seq[ModuleID] = Seq(core, enumeratum)
      }
    }

    object sttp extends LibGroup {
      lazy val circe: ModuleID =
        "com.softwaremill.sttp.client3" %% "circe" % Versions.sttp
      lazy val `fs2-backend`: ModuleID =
        "com.softwaremill.sttp.client3" %% "async-http-client-backend-fs2" % Versions.sttp
      override def all: Seq[ModuleID] = Seq(circe, `fs2-backend`)
    }
  }
  object eu {
    object timepit {
      lazy val `fs2-cron4s` = "eu.timepit" %% "fs2-cron-cron4s" % Versions.`fs2-cron4s`
    }
  }
  object org {
    lazy val flywaydb: ModuleID = "org.flywaydb" % "flyway-core" % Versions.flyway
    object typelevel {
      object cats {
        lazy val core = "org.typelevel"     %% "cats-core"     % Versions.cats
        lazy val effect = "org.typelevel"   %% "cats-effect"   % Versions.`cats-effect`
        lazy val log4cats = "org.typelevel" %% "log4cats-core" % Versions.log4cats
      }
    }

    object tpolecat {
      object doobie extends LibGroup {
        private def doobie(artifact: String): ModuleID =
          "org.tpolecat" %% s"doobie-$artifact" % Versions.doobie
        lazy val core = doobie("core")
        lazy val postgres = doobie("postgres")
        lazy val hikari = doobie("hikari")
        override def all: Seq[ModuleID] = Seq(core, postgres, hikari)
      }
    }
    object http4s extends LibGroup {
      private def http4s(artifact: String): ModuleID =
        "org.http4s" %% s"http4s-$artifact" % Versions.http4s

      lazy val dsl = http4s("dsl")
      lazy val server = http4s("ember-server")
      lazy val client = http4s("ember-client")
      lazy val circe = http4s("circe")
      override def all: Seq[ModuleID] = Seq(dsl, server, client, circe)
    }
  }

  object ch {
    object qos {
      lazy val logback = "ch.qos.logback" % "logback-classic" % Versions.logback
    }
  }
}

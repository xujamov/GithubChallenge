ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

lazy val root = (project in file("."))
  .settings(
    name := "Github"
  )

lazy val http4sVersion = "0.23.6"
lazy val circeVersion = "0.14.3"
lazy val doobieVersion = "1.0.0-M4"
lazy val cats = "2.9.0"
lazy val catsEffectVersion = "3.2.9"
lazy val fs2Version = "3.9.4"
lazy val scalaTestVersion = "3.2.10"
lazy val flyway = "9.16.0"
lazy val log4cats = "2.5.0"
lazy val logback = "1.4.7"
lazy val pureconfig = "0.17.2"
lazy val circeGenericExtras = "0.14.3"

libraryDependencies ++= Seq(
  "org.http4s"            %% "http4s-ember-server"   % http4sVersion,
  "org.http4s"            %% "http4s-ember-client"   % http4sVersion,
  "org.http4s"            %% "http4s-blaze-server"   % http4sVersion,
  "org.http4s"            %% "http4s-circe"          % http4sVersion,
  "org.http4s"            %% "http4s-dsl"            % http4sVersion,
  "io.circe"              %% "circe-generic"         % circeVersion,
  "io.circe"              %% "circe-parser"          % circeVersion,
  "co.fs2"                %% "fs2-core"              % fs2Version,
  "org.tpolecat"          %% "doobie-core"           % doobieVersion,
  "org.tpolecat"          %% "doobie-postgres"       % doobieVersion,
  "org.tpolecat"          %% "doobie-hikari"         % doobieVersion,
  "org.typelevel"         %% "cats-core"             % cats,
  "org.typelevel"         %% "cats-effect"           % catsEffectVersion,
  "org.typelevel"         %% "log4cats-core"         % log4cats,
  "org.scalatest"         %% "scalatest"             % scalaTestVersion % "test",
  "org.flywaydb"           % "flyway-core"           % flyway,
  "ch.qos.logback"         % "logback-classic"       % logback,
  "com.github.pureconfig" %% "pureconfig"            % pureconfig,
  "com.github.pureconfig" %% "pureconfig-enumeratum" % pureconfig,
  "io.circe"              %% "circe-generic-extras"  % circeGenericExtras,
)

scalacOptions ++= Seq(
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-deprecation", // Enable additional warnings when code uses deprecated elements.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred.
  "-language:experimental.macros", // Allow macro definition (besides implementation and application).
  "-language:higherKinds", // Allow higher-kinded types.
  "-language:reflectiveCalls",
  "-language:implicitConversions", // Allow definition of implicit functions called views.
  "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
  "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
  "-Xlint:option-implicit", // Option.apply used implicit view.
  "-Xlint:package-object-classes", // Class or object defined in package object.
  "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
  "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
  "-Ywarn-value-discard", // Warn when non-Unit expression results are unused.
  "-Yrangepos",
  "-Ymacro-annotations",
  "-Wconf:" +
    "cat=deprecation:ws," +
    "cat=other-match-analysis:error," +
    "cat=w-flag-value-discard:error," +
    "cat=w-flag-numeric-widen:error," +
    "cat=lint-infer-any:error," +
    "cat=w-flag-dead-code:error," +
    "cat=lint-type-parameter-shadow:error," +
    "msg=Reference to uninitialized value:error",
)
addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
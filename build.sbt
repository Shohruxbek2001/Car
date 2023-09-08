import sbt.Keys.libraryDependencies

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

lazy val root = (project in file("."))
  .settings(
    name := "car",
    scalacOptions ++= Seq(
      "-Ymacro-annotations"
    ),
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-core" % "0.23.23",
      "org.http4s" %% "http4s-ember-server" % "0.23.23",
      "org.http4s" %% "http4s-circe" % "0.23.23",
      "org.http4s" %% "http4s-dsl" % "0.23.23",
      "ch.qos.logback" % "logback-classic" % "1.4.11",
      "org.typelevel" %% "log4cats-slf4j" % "2.6.0",
      "org.tpolecat" %% "skunk-core" % "0.6.0",
      "io.circe" %% "circe-core" % "0.14.1",
      "io.circe" %% "circe-generic" % "0.14.1",
      "io.circe" %% "circe-parser" % "0.14.1"
    )
  )



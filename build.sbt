name in ThisBuild := "artamus"
version in ThisBuild := "0.1"
startYear in ThisBuild := Some(2019)
description in ThisBuild := "Music analysis client/server application - Photoshop for symbolic music"
licenses in ThisBuild := Seq("Apache 2.0" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
organization in ThisBuild := "nl.roelofruis"

scalaVersion in ThisBuild := "2.13.1"

lazy val dependencies = new {
  val scalaGuiceVersion = "4.2.6"
  val scalaLoggingVersion = "3.9.2"
  val slf4jSimpleVersion = "1.7.28"
  val sprayJsonVersion = "1.3.5"
  val microtestVersion = "0.7.1"

  val javaxInject     = "javax.inject" % "javax.inject" % "1"
  val scalaGuice      = "net.codingwell" %% "scala-guice" % scalaGuiceVersion
  val scalaLogging    = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
  val slf4jSimple     = "org.slf4j" % "slf4j-simple" % slf4jSimpleVersion
  val sprayJson       = "io.spray" %%  "spray-json" % sprayJsonVersion
  val microtest       = "com.lihaoyi" %% "utest" % microtestVersion
}

lazy val compilerFlags = Seq(
  scalacOptions ++= Seq(
    "-encoding", "UTF-8",
    "-unchecked",
    "-deprecation",
    "-Ywarn-dead-code",
    "-feature",
    "-language:implicitConversions",
  )
)

lazy val global = (project in file("."))
  .settings(compilerFlags)
  .aggregate(
    common,
    storage,
    client,
    server
  )

lazy val common = (project in file("common"))
  .settings(
    compilerFlags,
    libraryDependencies ++= Seq(
      dependencies.scalaGuice,
      dependencies.microtest % Test
    )
  )

lazy val storage = (project in file("storage"))
  .settings(
    compilerFlags,
    libraryDependencies ++= Seq(
      dependencies.scalaGuice // TODO: maybe try to remove?
    )
  )

lazy val client = (project in file("client"))
  .settings(
    compilerFlags,
    // Make sure (midi) libraries can get loaded in the correct way
    fork in run := true,
    connectInput in run := true,
    outputStrategy in run := Some(StdoutOutput),
    // Dependencies
    libraryDependencies ++= Seq(
      dependencies.scalaGuice,
      dependencies.scalaLogging,
      dependencies.slf4jSimple,
    )
  )
  .dependsOn(
    common,
    server // TODO: Refactor to remove this dependency!
  )

lazy val server = (project in file("server"))
  .settings(
    compilerFlags,
    // Dependencies
    libraryDependencies ++= Seq(
      dependencies.scalaGuice,
      dependencies.scalaLogging,
      dependencies.slf4jSimple,
      dependencies.sprayJson
    )
  )
  .dependsOn(storage, common)

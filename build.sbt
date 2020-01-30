import Dependencies._
import Scalac._

version in ThisBuild := "0.1"
startYear in ThisBuild := Some(2019)
licenses in ThisBuild := Seq("Apache 2.0" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
organization in ThisBuild := "nl.roelofruis"

scalaVersion in ThisBuild := "2.13.1"

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
    name := "artamus-common",
    description := "Common packages for Artamus",
    compilerFlags,
    libraryDependencies ++= Seq(
      dependencies.javaxInject,
      dependencies.findbugs,
      dependencies.microtest % Test
    )
  )

lazy val storage = (project in file("storage"))
  .settings(
    name := "storage",
    description := "Ligthweight in-memory/file storage",
    compilerFlags,
    libraryDependencies ++= Seq(
      dependencies.findbugs,
      dependencies.microtest % Test
    )
  )

lazy val client = (project in file("client"))
  .settings(
    name := "artamus-client",
    description := "A Music analysis client - part of Artamus",
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
      dependencies.scalaSwing
    )
  )
  .dependsOn(
    common,
    server // TODO: Refactor to remove this dependency!
  )

lazy val server = (project in file("server"))
  .settings(
    name := "artamus-server",
    description := "A Music analysis server - part of Artamus",
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

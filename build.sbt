import Dependencies._
import Scalac._

version in ThisBuild := "0.1"
startYear in ThisBuild := Some(2019)
licenses in ThisBuild := Seq("Apache 2.0" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
organization in ThisBuild := "nl.roelofruis"

scalaVersion in ThisBuild := "2.13.1"

lazy val artamus = (project in file("."))
  .settings(compilerFlags)
  .aggregate(
    web,
    core,
    common,
    storage,
    client,
    server
  )

lazy val common = (project in file("common"))
  .settings(
    name := "common",
    description := "Common packages",
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
    description := "Lightweight in-memory/file storage",
    compilerFlags,
    libraryDependencies ++= Seq(
      dependencies.findbugs,
      dependencies.microtest % Test
    )
  )

lazy val network = (project in file("network"))
  .settings(
    name := "network",
    description := "Socket communication",
    compilerFlags,
    libraryDependencies ++= Seq(
      dependencies.javaxInject,
      dependencies.findbugs,
      dependencies.microtest % Test
    )
  )

lazy val core = (project in file ("artamus-core"))
  .settings(
    name := "artamus-core",
    description := "Artamus core package",
    compilerFlags,
    libraryDependencies ++= Seq(
      dependencies.javaxInject,
      dependencies.findbugs,
      dependencies.microtest % Test
    )
  )
  .dependsOn(common)

lazy val web = (project in file("artamus-web"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "artamus-web",
    description := "Artamus web application",
    compilerFlags,

    // Dependencies
    libraryDependencies ++= Seq(
      dependencies.scalaLogging,
      dependencies.cask,
      dependencies.scalaTags,
      "org.scala-js" %%% "scalajs-dom" % "1.0.0",
    )
  )
  .dependsOn(core, storage)

lazy val client = (project in file("artamus-client"))
  .settings(
    name := "artamus-client",
    description := "A Music analysis client - part of Artamus",
    compilerFlags,

    // Makes sure (midi) libraries can get loaded in the correct way
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
  .dependsOn(core, network, common)

lazy val server = (project in file("artamus-server"))
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
  .dependsOn(core, network, storage, common)

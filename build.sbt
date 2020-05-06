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
    core,
    storage,
    client,
    server
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

lazy val app = (crossProject(JSPlatform, JVMPlatform) in file("artamus-app"))
  .settings(
    name := "artamus-app",
    description := "Artamus web application",
    compilerFlags,

    libraryDependencies ++= Seq(
      dependencies.scalaTags
    ),
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.0.0",
    )
  ).jvmSettings(
    libraryDependencies ++= Seq(
        dependencies.scalaLogging,
        dependencies.cask,
      )
  )

lazy val client = app.js.dependsOn(core)

lazy val server = app.jvm.dependsOn(core, storage)

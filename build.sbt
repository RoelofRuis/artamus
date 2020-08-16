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
  )

lazy val core = (project in file ("artamus-core"))
  .settings(
    name := "artamus-core",
    description := "Artamus core package",
    compilerFlags,
    libraryDependencies ++= Seq(
      dependencies.microtest % Test
    )
  )

lazy val applications = (project in file ("applications"))
  .settings(
    name := "applications",
    description := "Artamus source package",
    compilerFlags,
    libraryDependencies ++= Seq(
      dependencies.javaxInject,
      dependencies.sprayJson,
      dependencies.microtest % Test
    )
  )
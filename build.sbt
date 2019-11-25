name := "artamus"
version := "0.1"
startYear := Some(2019)
description := "Music analysis client/server application - Photoshop for symbolic music"
licenses := Seq("Apache 2.0" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))

scalaVersion := "2.13.1"

val scalaGuiceVersion = "4.2.6"
val scalaLoggingVersion = "3.9.2"
val slf4jSimpleVersion = "1.7.28"
val sprayJsonVersion = "1.3.5"

libraryDependencies ++= Seq(
  "net.codingwell" %% "scala-guice" % scalaGuiceVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
  "org.slf4j" % "slf4j-simple" % slf4jSimpleVersion,
  "io.spray" %%  "spray-json" % sprayJsonVersion
)

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-unchecked",
  "-deprecation",
  "-Ywarn-dead-code",
  "-feature",
  "-language:implicitConversions",
)

// Make sure (midi) libraries can get loaded in the correct way
fork in run := true
connectInput in run := true
outputStrategy in run := Some(StdoutOutput)

mainClass in (Compile, run) := Some("server.Main")
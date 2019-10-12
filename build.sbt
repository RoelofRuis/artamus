name := "artamus"
version := "0.1"
startYear := Some(2019)
description := "Music analysis client/server application - Photoshop for symbolic music"

scalaVersion := "2.12.8"

val scalaGuiceVersion = "4.2.2"
val scalaLoggingVersion = "3.9.2"
val slf4jSimpleVersion = "1.7.28"

libraryDependencies ++= Seq(
  "net.codingwell" %% "scala-guice" % scalaGuiceVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
  "org.slf4j" % "slf4j-simple" % slf4jSimpleVersion
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
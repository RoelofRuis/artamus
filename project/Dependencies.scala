import sbt._

object Dependencies {
  val dependencies = new {
    val scalaGuiceVersion = "4.2.6"
    val scalaLoggingVersion = "3.9.2"
    val slf4jSimpleVersion = "1.7.28"
    val sprayJsonVersion = "1.3.5"
    val microtestVersion = "0.7.1"

    val javaxInject     = "javax.inject" % "javax.inject" % "1"

    val scalaGuice      = "net.codingwell" %% "scala-guice" % scalaGuiceVersion
    val scalaLogging    = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
    val slf4jSimple     = "org.slf4j" % "slf4j-simple" % slf4jSimpleVersion
    val sprayJson       = "io.spray" %% "spray-json" % sprayJsonVersion
    val microtest       = "com.lihaoyi" %% "utest" % microtestVersion
  }
}

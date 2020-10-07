import sbt._

object Dependencies {
  val dependencies = new {
    val javaxInject  = "javax.inject"               %  "javax.inject"  % "1"

    val scalaGuice   = "net.codingwell"             %% "scala-guice"   % "4.2.6"
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
    val slf4jSimple  = "org.slf4j"                  %  "slf4j-simple"  % "1.7.28"
    val sprayJson    = "io.spray"                   %% "spray-json"    % "1.3.5"
    val microtest    = "com.lihaoyi"                %% "utest"         % "0.7.1"
    val fastparse    = "com.lihaoyi"                %% "fastparse"     % "2.2.2"
  }
}

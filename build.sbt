name := "streamcompose"

version := "0.1"

scalaVersion := "2.12.8"

val logbackVersion = "1.2.3"
val scalaGuiceVersion = "4.2.2"
val typesafeConfigVersion = "1.3.3"

libraryDependencies += "ch.qos.logback" % "logback-classic" % logbackVersion

libraryDependencies += "net.codingwell" %% "scala-guice" % scalaGuiceVersion

libraryDependencies += "com.typesafe" % "config" % typesafeConfigVersion

scalacOptions ++= Seq(
  "-feature",
  "-language:implicitConversions"
)
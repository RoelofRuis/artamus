name := "streamcompose"

version := "0.1"

scalaVersion := "2.12.8"

val scalaGuiceVersion = "4.2.2"
val typesafeConfigVersion = "1.3.3"

libraryDependencies += "net.codingwell" %% "scala-guice" % scalaGuiceVersion

libraryDependencies += "com.typesafe" % "config" % typesafeConfigVersion

scalacOptions ++= Seq(
  "-feature",
  "-language:implicitConversions"
)
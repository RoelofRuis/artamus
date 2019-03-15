name := "streamcompose"

version := "0.1"

scalaVersion := "2.12.8"

val scalaGuiceVersion = "4.2.2"
val typesafeConfigVersion = "1.3.3"

libraryDependencies += "net.codingwell" %% "scala-guice" % scalaGuiceVersion

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
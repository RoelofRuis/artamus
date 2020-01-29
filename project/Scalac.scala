import sbt.Keys.scalacOptions

object Scalac {
  val compilerFlags = Seq(
    scalacOptions ++= Seq(
      "-encoding", "UTF-8",
      "-unchecked",
      "-deprecation",
      "-Ywarn-dead-code",
      "-feature",
      "-language:implicitConversions",
    )
  )
}

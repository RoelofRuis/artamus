import sbt.Keys.scalacOptions

object Scalac {
  val compilerFlags = Seq(
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-explaintypes",
      "-feature",
      "-language:implicitConversions",
      "-unchecked",
      "-Xlint:infer-any",
      "-Xlint:type-parameter-shadow",
      "-Xlint:unsound-match",
      "-Ywarn-unused:imports",
      "-Ywarn-unused:locals",
      "-Ywarn-unused:params",
      "-Ywarn-unused:patvars",
      "-Ywarn-unused:privates",
      "-Ywarn-dead-code",
      "-Yno-imports",
    )
  )
}

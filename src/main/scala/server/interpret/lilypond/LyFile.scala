package server.interpret.lilypond

final case class LyFile(
  containers: Seq[Container],
  lyVersion: String,
  paperSize: String
) {

  def getStringContents: String = {
    val dynamicContent = containers.map(_.asString).mkString("\n")
    s"""\\version "$lyVersion"
      |
      |\\paper {
      |  #(set-paper-size "$paperSize")
      |}
      |
      |\\header {
      |  tagline = ##f
      |}
      |
      |\\score {
      |<<
      |$dynamicContent
      |>>
      |}
      |""".stripMargin
  }
}

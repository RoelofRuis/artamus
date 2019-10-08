package server.rendering.interpret.lilypond

final case class LyFile(
  staff: Staff,
  lyVersion: String = "2.18",
  paperSize: String = "a6landscape"
) {

  def getStringContents: String = {
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
      |${staff.asString}
      |>>
      |}
      |""".stripMargin
  }
}

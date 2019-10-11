package server.interpret.lilypond

final case class LyFile(
  staff: Staff,
  chords: ChordNames,
  lyVersion: String,
  paperSize: String
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
      |${chords.asString}
      |>>
      |}
      |""".stripMargin
  }
}

package server.interpret.lilypond

import music.symbol.collection.Track

object LilypondContexts {

  def file(
    contents: String,
    lyVersion: String,
    paperSize: String): String =
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
       |${contents}
       |>>
       |}
       |""".stripMargin

  def staff(track: Track): String =
    s"""\\new Staff {
       |\\numericTimeSignature
       |${new StaffIterator(track).stream}
       |}""".stripMargin

  def chords(track: Track): String =
    s"""\\new ChordNames {
       |\\chordmode {
       |${new ChordIterator(track).stream}
       |}
       |}""".stripMargin

}

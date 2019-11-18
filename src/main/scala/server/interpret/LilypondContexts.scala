package server.interpret

import music.domain.track.Track2

object LilypondContexts {

  import LilypondFormat._
  import music.glyph._

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

  def staff(track: Track2): String =
    s"""\\new Staff {
       |\\numericTimeSignature
       |${track.iterateStaffGlyphs.map(_.toLilypond).mkString("\n")}
       |}""".stripMargin

  def chords(track: Track2): String =
    s"""\\new ChordNames {
       |\\chordmode {
       |${track.iterateChordGlyphs.map(_.toLilypond).mkString("\n")}
       |}
       |}""".stripMargin

}

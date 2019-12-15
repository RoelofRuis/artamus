package server.interpret

import music.display.DisplayTrack

object LilypondContexts {

  import LilypondFormat._

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

  def staff(track: DisplayTrack): String =
    s"""\\new Staff {
       |\\numericTimeSignature
       |${track.staff.toLilypond}
       |}""".stripMargin

  def chords(track: DisplayTrack): String =
    s"""\\new ChordNames {
       |\\chordmode {
       |${track.chordStaff.toLilypond}
       |}
       |}""".stripMargin

}

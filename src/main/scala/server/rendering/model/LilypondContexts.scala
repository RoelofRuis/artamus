package server.rendering.model

import music.model.display.TrackDisplay

private[rendering] object LilypondContexts {

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

  def staff(track: TrackDisplay): String =
    s"""\\new Staff {
       |\\numericTimeSignature
       |${track.staff.toLilypond}
       |\\bar "|."
       |}""".stripMargin

  def chords(track: TrackDisplay): String =
    s"""\\new ChordNames {
       |\\chordmode {
       |${track.chordStaff.toLilypond}
       |}
       |}""".stripMargin

}

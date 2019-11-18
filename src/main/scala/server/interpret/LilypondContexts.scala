package server.interpret

import music.domain.track.Track2
import music.glyph.iteration.{ChordIterator, StaffIterator}
import music.math.temporal.Position

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

  def staff(track: Track2): String =
    s"""\\new Staff {
       |\\numericTimeSignature
       |${new StaffIterator(track).iterate(Position.ZERO).map(_.toLilypond).mkString("\n")}
       |}""".stripMargin

  def chords(track: Track2): String =
    s"""\\new ChordNames {
       |\\chordmode {
       |${new ChordIterator(track).iterate(Position.ZERO).map(_.toLilypond).mkString("\n")}
       |}
       |}""".stripMargin

}

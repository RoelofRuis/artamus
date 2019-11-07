package server.interpret

import music.glyph.iteration.{ChordIterator, StaffIterator}
import music.primitives.Position
import music.symbol.collection.Track

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

  def staff(track: Track): String =
    s"""\\new Staff {
       |\\numericTimeSignature
       |${new StaffIterator(track).iterate(Position.zero).map(_.toLilypond).mkString("\n")}
       |}""".stripMargin

  def chords(track: Track): String =
    s"""\\new ChordNames {
       |\\chordmode {
       |${new ChordIterator(track).iterate(Position.zero).map(_.toLilypond).mkString("\n")}
       |}
       |}""".stripMargin

}

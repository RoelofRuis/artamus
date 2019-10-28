package server.interpret.lilypond

import music.primitives.Position
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
       |${new StaffIterator(track).iterate(Position.zero).mkString("\n")}
       |}""".stripMargin

  def chords(track: Track): String =
    s"""\\new ChordNames {
       |\\chordmode {
       |${new ChordIterator(track).iterate(Position.zero).mkString("\n")}
       |}
       |}""".stripMargin

}
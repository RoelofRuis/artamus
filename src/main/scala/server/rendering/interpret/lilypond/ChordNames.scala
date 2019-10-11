package server.rendering.interpret.lilypond

import music.primitives._

final case class ChordNames(
  chords: Seq[SpelledChord]
) {

  import server.rendering.interpret.lilypond.LilypondFormat._

  def asString: String = {
    val chordString = chords.map(_.toLilypond).mkString(" ")

    s"""\\new ChordNames {
       |\\chordmode {
       |$chordString
       |}
       |}""".stripMargin
  }
}

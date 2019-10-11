package server.interpret.lilypond

import music.spelling.SpelledChord

final case class ChordNames(
  chords: Seq[SpelledChord]
) {

  import server.interpret.lilypond.LilypondFormat._

  def asString: String = {
    val chordString = chords.map(_.toLilypond).mkString(" ")

    s"""\\new ChordNames {
       |\\chordmode {
       |$chordString
       |}
       |}""".stripMargin
  }
}

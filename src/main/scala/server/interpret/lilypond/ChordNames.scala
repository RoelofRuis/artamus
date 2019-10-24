package server.interpret.lilypond

import music.collection.TrackSymbol
import music.symbols.Chord

final case class ChordNames(
  chords: Seq[TrackSymbol[Chord]]
) {

  import server.interpret.lilypond.LilypondFormat._

  def asString: String = {
    val chordString = chords.map(_.symbol.toLilypond).mkString(" ")

    s"""\\new ChordNames {
       |\\chordmode {
       |$chordString
       |}
       |}""".stripMargin
  }
}

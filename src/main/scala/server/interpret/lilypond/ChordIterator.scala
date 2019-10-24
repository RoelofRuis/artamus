package server.interpret.lilypond

import music.symbol.collection.Track
import music.primitives._
import music.symbol.Chord

class ChordIterator(track: Track) {

  import server.interpret.lilypond.LilypondFormat._

  private val chords = track.getSymbolTrack[Chord]

  def stream: Stream[String] = { // TODO: make iterator
    val pos = Position.zero // TODO: make argument later

    val initialChord = chords.readFirstAt(pos)

    def loop(pos: Position): Stream[String] = {
      chords.readNext(pos).headOption match {
        case None =>
          Stream.empty
        case Some(chord) =>
          val nextPos = chord.position
          // TODO: check of rusten tussengevoegd moeten worden (DIT IS EEN LILYPOND DING!)
          chord.symbol.toLilypond #:: loop(nextPos)
      }
    }

    initialChord match {
      case None => loop(pos)
      case Some(chord) => chord.symbol.toLilypond #:: loop(pos)
    }
  }

}

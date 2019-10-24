package server.interpret.lilypond

import music.symbol.collection.Track
import music.primitives._
import music.symbol.Chord

import scala.annotation.tailrec

class ChordIterator(track: Track) extends ContentIterator {

  import server.interpret.lilypond.LilypondFormat._

  private val chords = track.getSymbolTrack[Chord]

  @tailrec
  private def readNext(pos: Position): Option[(Position, String)] = {
    val nextChord = chords.readFirstNext(pos)
    if (nextChord.isEmpty) None
    else {
      val nextPos = nextChord.head.position
      nextChord.get.symbol.toLilypond match {
        case Some(lilyString) => Some((nextPos, lilyString))
        case None => readNext(nextPos)
      }
    }
  }

  def stream: Stream[String] = { // TODO: make iterator
    val pos = Position.zero // TODO: make argument later

    val initialChord = chords.readFirstAt(pos)

    def loop(pos: Position): Stream[String] = {
      readNext(pos) match {
        case None => Stream.empty
        case Some((nextPos, lilyString)) =>
          // TODO: check of rusten tussengevoegd moeten worden (DIT IS EEN LILYPOND DING!)
          lilyString #:: loop(nextPos)
      }
    }

    initialChord match {
      case None => loop(pos)
      case Some(chord) => chord.symbol.toLilypond.get #:: loop(pos)
    }
  }

}

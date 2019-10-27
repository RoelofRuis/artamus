package server.interpret.lilypond

import music.symbol.collection.Track
import music.primitives._
import music.symbol.Chord

import scala.annotation.tailrec

class ChordIterator(track: Track) {

  import server.interpret.lilypond.LilypondFormat._

  private val chords = track.select[Chord]

  def iterate(start: Position): Iterator[String] = {
    val window = Window(start, start)

    def loop(curWindow: Window): Iterator[String] = {
      readNext(curWindow) match {
        case None => Iterator.empty
        case Some((nextWindow, lilyString)) =>
          val difference = curWindow.durationUntil(nextWindow)
          if (difference.isNone) Iterator(lilyString) ++ loop(nextWindow)
          else Iterator(restToLilypond(difference, silent=true), lilyString) ++ loop(nextWindow)
      }
    }

    chords.firstAt(window.start) match { // TODO: this is comparable to readNext and should be combined
      case None => loop(window)
      case Some(chord) =>
        chord.symbol.toLilypond match {
          case Some(lilyString) => Iterator(lilyString) ++ loop(chord.window)
          case None => loop(window)
        }
    }
  }

  @tailrec
  private def readNext(window: Window): Option[(Window, String)] = {
    chords.firstNext(window.start) match {
      case None => None
      case Some(nextChord) =>
        nextChord.symbol.toLilypond match {
          case Some(lilyString) => Some(nextChord.window, lilyString)
          case None => readNext(nextChord.window)
        }
    }
  }

}

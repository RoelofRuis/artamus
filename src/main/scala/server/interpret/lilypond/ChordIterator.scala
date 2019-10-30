package server.interpret.lilypond

import music.symbol.collection.Track
import music.primitives._
import music.symbol.Chord

import scala.annotation.tailrec

class ChordIterator(track: Track) {

  import server.interpret.lilypond.LilypondFormat._

  private val chords = track.read[Chord]

  def iterate(start: Position): Iterator[String] = {
    val window = Window(start, start)

    def loop(curWindow: Window): Iterator[String] = {
      readNext(curWindow) match {
        case None => Iterator.empty
        case Some((nextWindow, lilyString)) =>
          curWindow.until(nextWindow) match {
            case None => Iterator(lilyString) ++ loop(nextWindow)
            case Some(diff) => Iterator(restToLilypond(diff.duration, silent=true), lilyString) ++ loop(nextWindow)
          }
      }
    }

    chords.firstAt(window.start) match {
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

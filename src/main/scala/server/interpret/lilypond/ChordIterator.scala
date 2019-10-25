package server.interpret.lilypond

import music.symbol.collection.Track
import music.primitives._
import music.symbol.Chord

import scala.annotation.tailrec

class ChordIterator(track: Track) extends ContentIterator {

  import server.interpret.lilypond.LilypondFormat._

  private val chords = track.getSymbolTrack[Chord]

  def stream: Stream[String] = { // TODO: make iterator
    val window = Window.zero // TODO: make argument later

    def loop(curWindow: Window): Stream[String] = {
      readNext(curWindow) match {
        case None => Stream.empty
        case Some((nextWindow, lilyString)) =>
          val difference = curWindow diff nextWindow
          if (difference == Duration.zero) lilyString #:: loop(nextWindow)
          else {
            restToLilypond(difference, silent=true) #:: lilyString #:: loop(nextWindow)
          }
      }
    }

    chords.readFirstAt(window.start) match {
      case None => loop(window)
      case Some(chord) => chord.symbol.toLilypond.get #:: loop(window)
    }
  }

  @tailrec
  private def readNext(window: Window): Option[(Window, String)] = {
    chords.readFirstNext(window.start) match {
      case None => None
      case Some(nextChord) =>
        nextChord.symbol.toLilypond match {
          case Some(lilyString) => Some(nextChord.window, lilyString)
          case None => readNext(nextChord.window)
        }
    }
  }

}

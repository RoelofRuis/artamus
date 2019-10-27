package server.interpret.lilypond

import music.primitives._
import music.symbol.collection.Track
import music.symbol.{Key, Note, TimeSignature}

import scala.annotation.tailrec

class StaffIterator(track: Track) {

  import music.analysis.TwelveToneEqualTemprament._
  import server.interpret.lilypond.LilypondFormat._

  private val timeSignatures = track.select[TimeSignature]
  private val keys = track.select[Key]
  private val notes = track.select[Note]

  @tailrec
  private def readNext(window: Window): Option[(Window, String)] = {
    notes.next(window.start) match {
      case Seq() => None
      case nextNotes =>
        val nextWindow = nextNotes.map(_.window).head
        nextNotes.map(_.symbol).toLilypond match {
          case Some(lilyString) => Some((nextWindow, lilyString))
          case None => readNext(nextWindow)
        }
    }
  }

  def stream: Stream[String] = { // TODO: make iterator
    val window = Window.zero // TODO: make argument later

    val initialTimeSignature = timeSignatures
      .firstAt(window.start)
      .map(_.symbol)
      .getOrElse(TimeSignature(TimeSignatureDivision.`4/4`))

    val initialKey = keys
      .firstAt(window.start)
      .map(_.symbol)
      .getOrElse(Key(PitchSpelling(Step(0), Accidental(0)), Scale.MAJOR))

    def loop(curWindow: Window, timeSignature: TimeSignature, key: Key): Stream[String] = {
      readNext(curWindow) match {
        case None => Stream.empty
        case Some((nextWindow, lilyString)) =>
          // TODO: update time signature and key
          val difference = curWindow.durationUntil(nextWindow)
          if (difference.isZero) lilyString #:: loop(nextWindow, timeSignature, key)
          else restToLilypond(difference, silent=false) #:: lilyString #:: loop(nextWindow, timeSignature, key)
      }
    }

    val initialElements = Stream(initialTimeSignature.toLilypond.get, initialKey.toLilypond.get)

    notes.at(window.start) match { // TODO: this is comparable to readNext and should be combined
      case Seq() => loop(window, initialTimeSignature, initialKey)
      case notes =>
        notes.map(_.symbol).toLilypond match {
          case Some(lilyString) => initialElements #::: lilyString #:: loop(notes.head.window, initialTimeSignature, initialKey)
          case None => loop(window, initialTimeSignature, initialKey)
        }
    }
  }

}

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

  def iterate(start: Position): Iterator[String] = {
    val window = Window(start, start)

    val initialTimeSignature = timeSignatures
      .firstAt(window.start)
      .map(_.symbol)
      .getOrElse(TimeSignature(TimeSignatureDivision.`4/4`))

    val initialKey = keys
      .firstAt(window.start)
      .map(_.symbol)
      .getOrElse(Key(PitchSpelling(Step(0), Accidental(0)), Scale.MAJOR))

    def loop(curWindow: Window, timeSignature: TimeSignature, key: Key): Iterator[String] = {
      readNext(curWindow) match {
        case None => Iterator.empty
        case Some((nextWindow, lilyString)) =>
          // TODO: update time signature and key
          val difference = curWindow.durationUntil(nextWindow)
          if (difference.isZero) Iterator(lilyString) ++ loop(nextWindow, timeSignature, key)
          else Iterator(restToLilypond(difference, silent=false)) ++ Iterator(lilyString) ++ loop(nextWindow, timeSignature, key)
      }
    }

    val initialElements = Iterator(initialTimeSignature.toLilypond.get, initialKey.toLilypond.get)

    notes.at(window.start) match {
      case Seq() => loop(window, initialTimeSignature, initialKey)
      case notes =>
        notes.map(_.symbol).toLilypond match {
          case Some(lilyString) => initialElements ++ Iterator(lilyString) ++ loop(notes.head.window, initialTimeSignature, initialKey)
          case None => loop(window, initialTimeSignature, initialKey)
        }
    }
  }

}

package server.interpret.lilypond

import music.primitives._
import music.symbol.collection.Track
import music.symbol.{Key, Note, TimeSignature}

import scala.annotation.tailrec

class StaffIterator(track: Track) {

  import music.analysis.TwelveToneEqualTemprament._
  import server.interpret.lilypond.LilypondFormat._

  private val timeSignatures = track.read[TimeSignature]
  private val keys = track.read[Key]
  private val notes = track.read[Note]

  case class Context(timeSignature: TimeSignature, key: Key)

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
    val context = initialContext(window)

    def loop(curWindow: Window, context: Context): Iterator[String] = {
      readNext(curWindow) match {
        case None => Iterator.empty
        case Some((nextWindow, lilyString)) =>

          val difference = curWindow.durationUntil(nextWindow)
          if (difference.isNone) Iterator(lilyString) ++ loop(nextWindow, context)
          else Iterator(restToLilypond(difference, silent=false)) ++ Iterator(lilyString) ++ loop(nextWindow, context)
      }
    }

    val initialElements = Iterator(context.timeSignature.toLilypond.get, context.key.toLilypond.get)

    if (notes.isEmpty) initialElements ++ Iterator(restToLilypond(Duration.WHOLE, silent=true))
    else {
      notes.at(window.start) match {
        case Seq() => loop(window, context)
        case notes =>
          notes.map(_.symbol).toLilypond match {
            case Some(lilyString) => initialElements ++ Iterator(lilyString) ++ loop(notes.head.window, context)
            case None => loop(window, context)
          }
      }
    }
  }

  def initialContext(window: Window): Context = {
    val initialTimeSignature = timeSignatures
      .firstAt(window.start)
      .map(_.symbol)
      .getOrElse(TimeSignature(TimeSignatureDivision.`4/4`))

    val initialKey = keys
      .firstAt(window.start)
      .map(_.symbol)
      .getOrElse(Key(PitchSpelling(Step(0), Accidental(0)), Scale.MAJOR))

    Context(initialTimeSignature, initialKey)
  }

}

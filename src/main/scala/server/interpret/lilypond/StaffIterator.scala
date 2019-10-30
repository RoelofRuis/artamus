package server.interpret.lilypond

import music.primitives._
import music.symbol.collection.Track
import music.symbol.{Key, Note, TimeSignature}

import scala.annotation.tailrec

class StaffIterator(track: Track) {

  import music.analysis.BarAnalysis._
  import music.analysis.TwelveToneEqualTemprament._
  import server.interpret.lilypond.LilypondFormat._

  private val timeSignatures = track.read[TimeSignature]
  private val keys = track.read[Key]
  private val notes = track.read[Note]

  case class Context(timeSignature: TimeSignature, key: Key)
  case class PositionIndicator(window: Window, isFirst: Boolean)

  def iterate(start: Position): Iterator[String] = {
    val window = Window(start, start)
    val positionIndicator = PositionIndicator(window, isFirst=true)
    val context = initialContext(window)

    def loop(pos: PositionIndicator, context: Context): Iterator[String] = {
      read(pos) match {
        case None => Iterator.empty
        case Some((nextWindow, lilyString)) =>

          val bar1 = timeSignatures.getBarForPosition(pos.window.start)
          println(bar1.toString)
          val bar2 = timeSignatures.getBarForPosition(pos.window.end)
          println(bar2.toString)

          val difference = pos.window.durationUntil(nextWindow)
          val nextPos = PositionIndicator(nextWindow, isFirst=false)
          if (difference.isNone) Iterator(lilyString) ++ loop(nextPos, context)
          else Iterator(restToLilypond(difference, silent=false)) ++ Iterator(lilyString) ++ loop(nextPos, context)
      }
    }

    val initialElements = Iterator(context.timeSignature.toLilypond.get, context.key.toLilypond.get)

    if (notes.isEmpty) initialElements ++ Iterator(restToLilypond(Duration.WHOLE, silent=true))
    else initialElements ++ loop(positionIndicator, context)
  }

  @tailrec
  private def read(pos: PositionIndicator): Option[(Window, String)] = {
    val elements = if (pos.isFirst) notes.at(pos.window.start) else notes.next(pos.window.start)
    elements match {
      case Seq() => None
      case nextNotes =>
        val nextWindow = nextNotes.map(_.window).head
        nextNotes.map(_.symbol).toLilypond match {
          case Some(lilyString) => Some((nextWindow, lilyString))
          case None => read(PositionIndicator(nextWindow, isFirst=false))
        }
    }
  }

  private def initialContext(window: Window): Context = {
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

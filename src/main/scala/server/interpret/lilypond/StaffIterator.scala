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
        case Some((nextWindow, lilyStrings)) =>
          val nextPos = PositionIndicator(nextWindow, isFirst=false)
          pos.window.until(nextWindow) match {
            case None => lilyStrings ++ loop(nextPos, context)
            case Some(diff) => Iterator(restToLilypond(diff.duration, silent=false)) ++ lilyStrings ++ loop(nextPos, context)
          }
      }
    }

    val initialElements = Iterator(context.timeSignature.toLilypond.get, context.key.toLilypond.get)

    if (notes.isEmpty) initialElements ++ Iterator(restToLilypond(Duration.WHOLE, silent=true))
    else initialElements ++ loop(positionIndicator, context)
  }

  @tailrec
  private def read(pos: PositionIndicator): Option[(Window, Iterator[String])] = {
    val elements = if (pos.isFirst) notes.at(pos.window.start) else notes.next(pos.window.start)
    elements match {
      case Seq() => None
      case nextNotes =>
        val nextWindow = nextNotes.map(_.window).head

        val pitches = nextNotes.map(_.symbol).flatMap(_.scientificPitch)
        val fittedDurations = timeSignatures.fitToBars(nextWindow).map(_.duration)

        val lilyStrings = fittedDurations
          .zipWithIndex
          .map { case (dur, i) => NoteGroup(dur, pitches, i != (fittedDurations.size - 1)) }
          .flatMap(_.toLilypond)
          .toIterator

        if (lilyStrings.isEmpty) read(PositionIndicator(nextWindow, isFirst=false))
        else Some((nextWindow, lilyStrings))
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

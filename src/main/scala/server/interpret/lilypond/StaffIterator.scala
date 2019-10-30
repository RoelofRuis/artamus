package server.interpret.lilypond

import music.primitives._
import music.symbol.collection.Track
import music.symbol.{Key, Note, TimeSignature}

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

    val initialElements = Iterator(context.timeSignature.toLilypond.get, context.key.toLilypond.get)

    initialElements ++ read(positionIndicator)
  }

  private def read(pos: PositionIndicator): Iterator[String] = {
    val elements = if (pos.isFirst) notes.at(pos.window.start) else notes.next(pos.window.start)
    elements match {
      case Seq() =>
        if (pos.isFirst) Iterator(restToLilypond(Duration.WHOLE, silent=true))
        else Iterator.empty

      case nextNotes =>
        // windowing
        val nextWindow = nextNotes.map(_.window).head

        // rests
        val rests = pos.window.until(nextWindow) match {
          case None => Iterator.empty
          case Some(diff) =>
            timeSignatures
              .fitToBars(diff)
              .map(window => restToLilypond(window.duration, silent=false))
              .toIterator
        }

        // pitches
        val pitches = nextNotes.map(_.symbol).flatMap(_.scientificPitch)
        val fittedDurations = timeSignatures.fitToBars(nextWindow).map(_.duration)

        val lilyStrings = fittedDurations
          .zipWithIndex
          .map { case (dur, i) => NoteGroup(dur, pitches, i != (fittedDurations.size - 1)) }
          .flatMap(_.toLilypond)
          .toIterator

        val nextPos = PositionIndicator(nextWindow, isFirst=false)
        if (lilyStrings.isEmpty) rests ++ read(nextPos)
        else rests ++ lilyStrings ++ read(nextPos)
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

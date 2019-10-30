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

  def iterate(start: Position): Iterator[String] = {
    val window = Window(start, start)
    val context = initialContext(window)

    val initialElements = Iterator(context.timeSignature.toLilypond.get, context.key.toLilypond.get)

    if (notes.isEmpty) initialElements ++ Iterator(restToLilypond(Duration.WHOLE, silent=true))
    else initialElements ++ read(window, readFrom=false)
  }

  private def read(window: Window, readFrom: Boolean = true): Iterator[String] = {
    val elements = if (readFrom) notes.next(window.start) else notes.at(window.start)
    elements match {
      case Seq() =>
        if (readFrom) Iterator.empty
        else read(window)

      case nextNotes =>
        // windowing
        val nextWindow = nextNotes.map(_.window).head

        // rests
        val rests = window.until(nextWindow) match {
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

        if (lilyStrings.isEmpty) rests ++ read(nextWindow)
        else rests ++ lilyStrings ++ read(nextWindow)
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

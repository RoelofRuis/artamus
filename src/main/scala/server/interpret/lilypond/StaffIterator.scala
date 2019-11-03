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

    val initialElements = Iterator(context.timeSignature.toLilypond, context.key.toLilypond)

    initialElements ++ read(window, readFrom=false)
  }

  private def read(window: Window, readFrom: Boolean = true): Iterator[String] = {
    val elements = if (readFrom) notes.next(window.start) else notes.at(window.start)
    elements match {
      case Seq() =>
        if (readFrom)
          // fill bar
          WriteableDuration
            .from(timeSignatures.fillBarFrom(window).duration)
            .map(restToLilypond(_, silent=false))
            .toIterator

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
              .flatMap(window => WriteableDuration.from(window.duration))
              .map(writableDuration => restToLilypond(writableDuration, silent=false))
              .toIterator
        }

        // pitches
        val pitches = nextNotes.map(_.symbol).flatMap(_.scientificPitch)
        val fittedDurations =
          timeSignatures
            .fitToBars(nextWindow)
            .flatMap(window => WriteableDuration.from(window.duration))

        val lilyStrings = fittedDurations
          .zipWithIndex
          .map { case (dur, i) => WriteableNoteGroup(dur, pitches, i != (fittedDurations.size - 1)) }
          .map(_.toLilypond)
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

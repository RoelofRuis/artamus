package music.glyph

import music.glyph
import music.primitives._
import music.symbol.collection.Track
import music.symbol.{Key, Note, TimeSignature}

class StaffIterator(track: Track) {

  import music.analysis.BarAnalysis._
  import music.analysis.TwelveToneTuning._

  private val timeSignatures = track.read[TimeSignature]
  private val keys = track.read[Key]
  private val notes = track.read[Note]

  case class Context(timeSignature: TimeSignature, key: Key)

  def iterate(start: Position): Iterator[Glyph] = {
    val window = Window(start, start)
    val context = initialContext(window)

    val initialElements = Iterator(
      TimeSignatureGlyph(context.timeSignature.division),
      KeyGlyph(context.key.root, context.key.scale)
    )

    initialElements ++ read(window, readFrom=false)
  }

  private def read(window: Window, readFrom: Boolean = true): Iterator[Glyph] = {
    val elements = if (readFrom) notes.next(window.start) else notes.at(window.start)
    elements match {
      case Seq() =>
        if (readFrom)
          // fill bar
          PrintableDuration
            .from(timeSignatures.fillBarFrom(window).duration)
            .map(RestGlyph(_, silent=false))
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
              .flatMap(window => PrintableDuration.from(window.duration))
              .map(glyph.RestGlyph(_, silent=false))
              .toIterator
        }

        // pitches
        val pitches = nextNotes.map(_.symbol).flatMap(_.scientificPitch)
        val fittedDurations =
          timeSignatures
            .fitToBars(nextWindow)
            .flatMap(window => PrintableDuration.from(window.duration))

        val lilyStrings = fittedDurations
          .zipWithIndex
          .map { case (dur, i) => NoteGroupGlyph(dur, pitches, i != (fittedDurations.size - 1)) }
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

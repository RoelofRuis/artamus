package music.glyph

import music.glyph
import music.primitives._
import music.symbol.collection.Track
import music.symbol.{Key, Note, TimeSignature}

class StaffIterator(track: Track) {

  import music.analysis.BarAnalysis._
  import music.analysis.TwelveToneTuning._

  private val timeSignatures = track.read[TimeSignature]()
  private val keys = track.read[Key]()
  private val notes = track.readGrouped[Note]()

  case class Context(timeSignature: TimeSignature, key: Key)

  def iterate(start: Position): Iterator[Glyph] = {
    val window = Window.instantAt(start)
    val context = initialContext(window)

    val initialElements = Iterator( // TODO: these should probably come from their own iterator
      TimeSignatureGlyph(context.timeSignature.division),
      KeyGlyph(context.key.root, context.key.scale)
    )

    initialElements ++ read(window)
  }

  private def read(window: Window): Iterator[Glyph] = {
    notes.nextOption() match {
      case None =>
        PrintableDuration
          .from(track.fillBarFrom(window).duration)
          .map(RestGlyph(_, silent=false))
          .iterator

      case Some(nextNotes) =>
        // windowing
        val nextWindow = nextNotes.map(_.window).head

        // rests
        val rests = window.until(nextWindow) match {
          case None => Iterator.empty
          case Some(diff) =>
            track
              .fitToBars(diff)
              .flatMap(window => PrintableDuration.from(window.duration))
              .map(glyph.RestGlyph(_, silent=false))
              .iterator
        }

        // pitches
        val pitches = nextNotes.map(_.symbol).flatMap(_.scientificPitch)
        val fittedDurations =
          track
            .fitToBars(nextWindow)
            .flatMap(window => PrintableDuration.from(window.duration))

        val lilyStrings = fittedDurations
          .zipWithIndex
          .map { case (dur, i) => NoteGroupGlyph(dur, pitches, i != (fittedDurations.size - 1)) }
          .iterator

        if (lilyStrings.isEmpty) rests ++ read(nextWindow)
        else rests ++ lilyStrings ++ read(nextWindow)
    }
  }

  private def initialContext(window: Window): Context = {
    val initialTimeSignature = timeSignatures
      .headOption
      .map(_.symbol)
      .getOrElse(TimeSignature(TimeSignatureDivision.`4/4`))

    val initialKey = keys
      .headOption
      .map(_.symbol)
      .getOrElse(Key(PitchSpelling(Step(0), Accidental(0)), Scale.MAJOR))

    Context(initialTimeSignature, initialKey)
  }

}

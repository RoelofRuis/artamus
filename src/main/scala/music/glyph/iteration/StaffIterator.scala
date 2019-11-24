package music.glyph.iteration

import music.analysis.NoteValueConversion
import music.domain.track.Track
import music.glyph
import music.glyph._
import music.math.temporal.{Position, Window}
import music.primitives._
import music.domain.track.symbol.{Key, Note}

private[glyph] class StaffIterator(track: Track) {

  import music.analysis.TwelveToneTuning._

  private val keys = track.read[Key]()
  private val notes = track.readGrouped[Note]()

  def iterate(start: Position): Iterator[Glyph] = {
    val window = Window.instantAt(start)
    val initialKey = keys
      .headOption
      .map(_.symbol)
      .getOrElse(Key(PitchSpelling(Step(0), Accidental(0)), Scale.MAJOR))

    val initialElements = Iterator( // TODO: these should probably come from their own iterator
      TimeSignatureGlyph(track.bars.initialTimeSignature.division),
      KeyGlyph(initialKey.root, initialKey.scale)
    )

    initialElements ++ read(window)
  }

  private def read(window: Window): Iterator[Glyph] = {
    notes.nextOption() match {
      case None =>
        NoteValueConversion
          .from(track.bars.fillBarFrom(window).duration)
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
              .bars
              .fit(diff)
              .flatMap(window => NoteValueConversion.from(window.duration))
              .map(glyph.RestGlyph(_, silent=false))
              .iterator
        }

        // pitches
        val pitches = nextNotes.map(_.symbol).flatMap(_.scientificPitch)
        val fittedDurations =
          track
            .bars
            .fit(nextWindow)
            .flatMap(window => NoteValueConversion.from(window.duration))

        val lilyStrings = fittedDurations
          .zipWithIndex
          .map { case (dur, i) => NoteGroupGlyph(dur, pitches, i != (fittedDurations.size - 1)) }
          .iterator

        if (lilyStrings.isEmpty) rests ++ read(nextWindow)
        else rests ++ lilyStrings ++ read(nextWindow)
    }
  }

}

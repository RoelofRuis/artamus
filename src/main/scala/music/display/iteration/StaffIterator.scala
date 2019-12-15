package music.display.iteration

import music.analysis.{NoteValueConversion, TwelveTonePitchSpelling}
import music.display._
import music.domain.track.Track
import music.math.temporal.{Position, Window}
import music.primitives.{Key, ScientificPitch}

private[display] class StaffIterator(track: Track) {

  import music.display.neww.Bars._

  private val notes = track.notes.readGroups
  private val initialKey: Key = track.keys.initialKey

  def iterate(start: Position): Iterator[Glyph] = {
    val window = Window.instantAt(start)

    val initialElements = Iterator(
      TimeSignatureGlyph(track.timeSignatures.initialTimeSignature.division),
      KeyGlyph(initialKey.root, initialKey.scale)
    )

    initialElements ++ read(window)
  }

  private def read(window: Window): Iterator[Glyph] = {
    notes.nextOption() match {
      case None =>
        NoteValueConversion
          .from(track.timeSignatures.fillBarFrom(window).duration)
          .map(RestGlyph(_, silent=false))
          .iterator

      case Some(nextGroup) =>
        // windowing
        val nextWindow = nextGroup.window

        // rests
        val rests = window.until(nextWindow) match {
          case None => Iterator.empty
          case Some(diff) =>
            track
              .timeSignatures
              .fit(diff)
              .flatMap(window => NoteValueConversion.from(window.duration))
              .map(RestGlyph(_, silent=false))
              .iterator
        }

        // pitches
        // TODO: Use the 'active' key instead of initial key.
        val pitches: Seq[ScientificPitch] = nextGroup.notes.map(TwelveTonePitchSpelling.spellNote(_, initialKey))
        val fittedDurations =
          track
            .timeSignatures
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

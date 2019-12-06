package music.glyph.iteration

import music.analysis.NoteValueConversion
import music.domain.track.Track
import music.glyph
import music.glyph._
import music.math.temporal.{Position, Window}

private[glyph] class StaffIterator(track: Track) {

  private val notes = track.notes.readGroups

  def iterate(start: Position): Iterator[Glyph] = {
    val window = Window.instantAt(start)
    val initialKey = track.keys.initialKey

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

      case Some(nextGroup) =>
        // windowing
        val nextWindow = nextGroup.window

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
        val pitches = nextGroup.notes.flatMap(_.scientificPitch)
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

package music

import music.domain.track.Track
import music.display.iteration.{ChordIterator, StaffIterator}
import music.math.temporal.Position

package object display {

  @deprecated
  implicit class GlyphOps(track: Track) {

    def iterateStaffGlyphs: Iterator[Glyph] = new StaffIterator(track).iterate(Position.ZERO)
    def iterateChordGlyphs: Iterator[Glyph] = new ChordIterator(track).iterate(Position.ZERO)

  }

}

package music

import music.domain.track.Track
import music.glyph.iteration.{ChordIterator, StaffIterator}
import music.math.temporal.Position

package object glyph {

  implicit class GlyphOps(track: Track) {

    def iterateStaffGlyphs: Iterator[Glyph] = new StaffIterator(track).iterate(Position.ZERO)
    def iterateChordGlyphs: Iterator[Glyph] = new ChordIterator(track).iterate(Position.ZERO)

  }

}

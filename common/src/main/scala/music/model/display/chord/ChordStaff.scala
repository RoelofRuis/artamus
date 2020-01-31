package music.model.display.chord

import music.model.display.Staff

final case class ChordStaff(glyphs: Iterator[ChordStaffGlyph]) extends Staff

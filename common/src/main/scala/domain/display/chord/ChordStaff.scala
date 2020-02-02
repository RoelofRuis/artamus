package domain.display.chord

import domain.display.Staff

final case class ChordStaff(glyphs: Iterator[ChordStaffGlyph]) extends Staff

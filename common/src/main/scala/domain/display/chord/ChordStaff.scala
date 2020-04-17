package domain.display.chord

import domain.display.Staff

final case class ChordStaff(glyphs: Seq[ChordStaffGlyph]) extends Staff

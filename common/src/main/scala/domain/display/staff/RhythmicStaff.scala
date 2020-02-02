package domain.display.staff

import domain.display.Staff

final case class RhythmicStaff(glyphs: Iterator[StaffGlyph]) extends Staff

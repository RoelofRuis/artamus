package music.model.display.staff

import music.model.display.Staff

final case class RhythmicStaff(glyphs: Iterator[StaffGlyph]) extends Staff

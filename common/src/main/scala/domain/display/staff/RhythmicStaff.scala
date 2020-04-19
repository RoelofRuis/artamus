package domain.display.staff

import domain.display.glyph.StaffGlyphFamily.StaffGlyph
import domain.display.glyph.Glyphs.Glyph

final case class RhythmicStaff(
  glyphs: Seq[Glyph[StaffGlyph]]
) extends Staff

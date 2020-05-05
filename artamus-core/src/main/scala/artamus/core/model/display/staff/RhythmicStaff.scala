package artamus.core.model.display.staff

import artamus.core.model.display.glyph.StaffGlyphFamily.StaffGlyph
import artamus.core.model.display.glyph.Glyphs.Glyph

final case class RhythmicStaff(
  glyphs: Seq[Glyph[StaffGlyph]]
) extends Staff

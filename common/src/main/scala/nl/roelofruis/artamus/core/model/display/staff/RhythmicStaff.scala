package nl.roelofruis.artamus.core.model.display.staff

import nl.roelofruis.artamus.core.model.display.glyph.StaffGlyphFamily.StaffGlyph
import nl.roelofruis.artamus.core.model.display.glyph.Glyphs.Glyph

final case class RhythmicStaff(
  glyphs: Seq[Glyph[StaffGlyph]]
) extends Staff

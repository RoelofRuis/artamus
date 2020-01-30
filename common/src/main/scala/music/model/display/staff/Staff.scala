package music.model.display.staff

final case class Staff(
  clef: Clef,
  glyphs: Iterator[StaffGlyph]
)

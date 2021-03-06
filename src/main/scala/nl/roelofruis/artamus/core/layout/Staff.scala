package nl.roelofruis.artamus.core.layout

sealed trait Staff

object Staff {

  type StaffGroup = Seq[Staff]

  final case class ChordStaff(
    glyphs: Seq[Glyph[ChordStaffGlyph]]
  ) extends Staff

  final case class NoteStaff(
    glyphs: Seq[Glyph[StaffGlyph]]
  ) extends Staff

  final case class RNAStaff(
    glyphs: Seq[Glyph[RNAStaffGlyph]]
  ) extends Staff

}

package nl.roelofruis.artamus.core.layout

sealed trait Staff

object Staff {

  type StaffGroup = Seq[Staff]

  final case class ChordStaff(
    glyphs: Seq[Glyph[ChordStaffGlyph]]
  ) extends Staff

}

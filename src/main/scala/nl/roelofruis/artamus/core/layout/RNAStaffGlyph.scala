package nl.roelofruis.artamus.core.layout

import nl.roelofruis.artamus.core.track.Pitched.{Degree, Key}

sealed trait RNAStaffGlyph

object RNAStaffGlyph {

  final case class DegreeGlyph(
    degree: Degree
  ) extends RNAStaffGlyph

  final case class KeyIndicatorGlyph(
    key: Key
  ) extends RNAStaffGlyph

  final case class RNARestGlyph() extends RNAStaffGlyph

}


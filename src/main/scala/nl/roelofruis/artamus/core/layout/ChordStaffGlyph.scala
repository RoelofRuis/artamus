package nl.roelofruis.artamus.core.layout

import nl.roelofruis.artamus.core.track.Pitched.{PitchDescriptor, Quality}

sealed trait ChordStaffGlyph

object ChordStaffGlyph {

  final case class ChordNameGlyph(
    root: PitchDescriptor,
    quality: Quality
  ) extends ChordStaffGlyph

  final case class ChordRestGlyph() extends ChordStaffGlyph

}

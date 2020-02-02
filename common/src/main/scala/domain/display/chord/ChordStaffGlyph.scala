package domain.display.chord

import domain.primitives.{Function, NoteValue, PitchSpelling}

sealed trait ChordStaffGlyph

object ChordStaffGlyph {

  final case class ChordNameGlyph(
    duration: NoteValue,
    root: PitchSpelling,
    functions: Set[Function]
  ) extends ChordStaffGlyph

  final case class ChordRestGlyph(
    duration: NoteValue,
  ) extends ChordStaffGlyph

}

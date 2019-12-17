package music.domain.display.chord

import music.domain.primitives.{Function, NoteValue, PitchSpelling}

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
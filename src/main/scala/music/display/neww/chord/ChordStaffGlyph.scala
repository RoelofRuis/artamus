package music.display.neww.chord

import music.primitives.{NoteValue, PitchSpelling, Function}

sealed trait ChordStaffGlyph

object ChordStaffGlyph {

  final case class ChordNameGlyph(
    duration: NoteValue,
    root: PitchSpelling,
    functions: Set[Function]
  ) extends ChordStaffGlyph

}

package music.glyph

import music.primitives.{Function, NoteValue, PitchSpelling}

final case class ChordGlyph(
  duration: NoteValue,
  root: PitchSpelling,
  functions: Set[Function]
) extends Glyph

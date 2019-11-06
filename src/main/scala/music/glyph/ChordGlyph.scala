package music.glyph

import music.primitives.{Function, NoteValue, PitchSpelling}

import scala.collection.SortedSet

final case class ChordGlyph(
  duration: NoteValue,
  root: PitchSpelling,
  functions: SortedSet[Function]
) extends Glyph

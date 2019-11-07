package music.glyph

import music.primitives.{NoteValue, ScientificPitch}

final case class NoteGroupGlyph(
  duration: NoteValue,
  notes: Seq[ScientificPitch],
  tieToNext: Boolean
) extends Glyph {

  def isEmpty: Boolean = notes.isEmpty
  def isChord: Boolean = notes.size > 1

}

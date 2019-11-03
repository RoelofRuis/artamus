package music.glyph

import music.primitives.ScientificPitch

final case class NoteGroupGlyph(
  duration: PrintableDuration,
  notes: Seq[ScientificPitch],
  tieToNext: Boolean
) extends Glyph {

  def isEmpty: Boolean = notes.isEmpty
  def isChord: Boolean = notes.size > 1

}

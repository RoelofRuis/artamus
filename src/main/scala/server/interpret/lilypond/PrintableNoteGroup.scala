package server.interpret.lilypond

import music.primitives.ScientificPitch

case class PrintableNoteGroup(
  duration: PrintableDuration,
  notes: Seq[ScientificPitch],
  tieToNext: Boolean
) {

  def isEmpty: Boolean = notes.isEmpty
  def isChord: Boolean = notes.size > 1

}

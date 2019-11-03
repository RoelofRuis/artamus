package server.interpret.lilypond

import music.primitives.ScientificPitch

case class NoteGroup(duration: WriteableDuration, notes: Seq[ScientificPitch], tieToNext: Boolean) {

  def isEmpty: Boolean = notes.isEmpty
  def isChord: Boolean = notes.size > 1

}

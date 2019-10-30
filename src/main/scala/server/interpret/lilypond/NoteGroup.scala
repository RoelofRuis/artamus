package server.interpret.lilypond

import music.primitives.{Duration, ScientificPitch}

case class NoteGroup(duration: Duration, notes: Seq[ScientificPitch], tieToNext: Boolean) {

  def isEmpty: Boolean = notes.isEmpty
  def isChord: Boolean = notes.size > 1

}

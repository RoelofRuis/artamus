package domain.record

final case class Recording(notes: Seq[RawMidiNote] = Seq()) {
  def recordNote(note: RawMidiNote): Recording = copy(notes = notes :+ note)
}

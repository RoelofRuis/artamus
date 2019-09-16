package music.symbolic

final case class MidiNoteNumber(value: Int) {
  // TODO: use AbsoluteRelativePair mathematics here
  def diff(other: MidiNoteNumber): Accidental = Accidental(other.value - value)
}

package music

final case class MidiNoteNumber(value: Int) {
  def +(other: Int): MidiNoteNumber = MidiNoteNumber(value + other)

  def diff(other: MidiNoteNumber): Accidental = Accidental(other.value - value)

}

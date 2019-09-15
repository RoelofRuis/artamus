package music.symbolic

final case class PitchClass(value: Int) extends AnyVal {
  def ==(other: PitchClass): Boolean = value == other.value
}

object PitchClass {

  def apply(i: MidiNoteNumber): PitchClass = PitchClass(i.value % 12)

}
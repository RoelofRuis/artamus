package music

final case class PitchClass(value: Int) extends AnyVal

object PitchClass {

  def apply(i: MidiNoteNumber): PitchClass = PitchClass(i.value % 12)

}
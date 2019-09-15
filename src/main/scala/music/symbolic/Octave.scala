package music.symbolic

final case class Octave private (value: Int) extends AnyVal

object Octave {

  def apply(i: MidiNoteNumber): Octave = new Octave((i.value / 12) - 1)

}
package music.primitives

final case class Octave(value: Int) {
  def +(i: Int): Octave = Octave(value + i)
}
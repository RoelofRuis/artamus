package music.math

import music.symbolic.{Accidental, Duration, MidiNoteNumber, Position}

/**
  * Abstraction over some absolute value with type A, and its difference with type R.
  *
  * @tparam A The abstract value type
  * @tparam R The relative value type
  */
trait AbsoluteRelativePair[A, R] {

  def difference(a1: A, a2: A): R

  def +|(a: A, r: R): A

  def -|(a: A, r: R): A

  def +(r1: R, r2: R): R

  def -(r1: R, r2: R): R

}

object AbsoluteRelativePair {

  val positionDurationPair: AbsoluteRelativePair[Position, Duration] = new AbsoluteRelativePair[Position, Duration] {
    override def difference(a1: Position, a2: Position): Duration = Duration(a1.value - a2.value)
    override def +|(a: Position, r: Duration): Position = Position(a.value + r.value)
    override def -|(a: Position, r: Duration): Position = Position(a.value - r.value)
    override def +(r1: Duration, r2: Duration): Duration = Duration(r1.value + r2.value)
    override def -(r1: Duration, r2: Duration): Duration = Duration(r1.value + r2.value)
  }

  val midiNoteNumberAccidentalPair: AbsoluteRelativePair[MidiNoteNumber, Accidental] =
    new AbsoluteRelativePair[MidiNoteNumber, Accidental] {
      override def difference(a1: MidiNoteNumber, a2: MidiNoteNumber): Accidental = Accidental(a1.value - a2.value)
      override def +|(a: MidiNoteNumber, r: Accidental): MidiNoteNumber = MidiNoteNumber(a.value + r.value)
      override def -|(a: MidiNoteNumber, r: Accidental): MidiNoteNumber = MidiNoteNumber(a.value - r.value)
      override def +(r1: Accidental, r2: Accidental): Accidental = Accidental(r1.value + r2.value)
      override def -(r1: Accidental, r2: Accidental): Accidental = Accidental(r1.value - r2.value)
    }

}
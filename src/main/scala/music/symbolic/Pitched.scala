package music.symbolic

object Pitched {

  final case class MidiNoteNumber(value: Int)

  trait ExactPitch

  final case class Step(value: Int)

  final case class Accidental(value: Int)

  final case class PitchClass(value: Int) extends ExactPitch

  final case class Spelled(step: Step, accidental: Accidental) extends ExactPitch

  case class Interval(pc: PitchClass, s: Step)

  case class Function(pc: PitchClass, s: Step) extends Comparable[Function] {
    override def compareTo(o: Function): Int = {
      val stepCompare = s.value.compare(o.s.value)
      if (stepCompare != 0) stepCompare
      else pc.value.compare(o.pc.value)
    }
  }

  case class Octave(value: Int)

  case class Pitch[A <: ExactPitch](octave: Octave, p: A)

  case class Chord(root: PitchClass, functions: Seq[Function])

}

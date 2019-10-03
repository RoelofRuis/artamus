package music.symbolic

import music.symbolic.temporal.Duration

package object pitch {

  final case class MidiNoteNumber(value: Int)

  final case class Step(value: Int)

  final case class Accidental(value: Int)

  final case class PitchClass(value: Int)

  final case class SpelledPitch(step: Step, accidental: Accidental)

  final case class SpelledNote(
    duration: Duration,
    octave: Octave,
    pitch: SpelledPitch
  )

  case class Interval(pc: PitchClass, step: Step)

  case class Function(pc: PitchClass, s: Step) extends Comparable[Function] {
    override def compareTo(o: Function): Int = {
      val stepCompare = s.value.compare(o.s.value)
      if (stepCompare != 0) stepCompare
      else pc.value.compare(o.pc.value)
    }
  }

  case class Octave(value: Int)

  case class Pitch(octave: Octave, pitchClass: PitchClass)

  case class Chord(root: PitchClass, functions: Seq[Function])

}

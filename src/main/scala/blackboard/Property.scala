package blackboard

import music.symbolic.pitch.{Octave, PitchClass}
import music.symbolic.symbol.{Key, TimeSignature}
import music.symbolic.temporal.Duration

trait Property[A]

// TODO: these should be moved to the package using them
object Property {

  implicit val pitchClassProp: Property[PitchClass] = new Property[PitchClass] {}
  implicit val octaveProp: Property[Octave] = new Property[Octave] {}
  implicit val durationProp: Property[Duration] = new Property[Duration] {}
  implicit val timesigProp: Property[TimeSignature] = new Property[TimeSignature] {}
  implicit val keyProp: Property[Key] = new Property[Key] {}

}

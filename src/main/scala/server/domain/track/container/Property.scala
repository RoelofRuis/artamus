package server.domain.track.container

import music.symbolic.pitch.{Chord, Octave, PitchClass}
import music.symbolic.symbol.{Key, TimeSignature}
import music.symbolic.temporal.Duration

trait Property[A]

object Property {

  implicit val pitchClassProp: Property[PitchClass] = new Property[PitchClass] {}
  implicit val octaveProp: Property[Octave] = new Property[Octave] {}
  implicit val durationProp: Property[Duration] = new Property[Duration] {}

  implicit val chordProp: Property[Chord] = new Property[Chord] {}

  implicit val timesigProp: Property[TimeSignature] = new Property[TimeSignature] {}
  implicit val keyProp: Property[Key] = new Property[Key] {}

}

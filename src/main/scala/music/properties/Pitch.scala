package music.properties

import music.math.ScaleMath
import music.{MidiNoteNumber, MidiPitch, Octave, ScientificPitch}

object Pitch {

  trait HasExactPitch[A] {
    def getMidiNoteNumber(pitch: A): MidiNoteNumber
  }

  implicit val midiPitchNumberHasExactPitch: HasExactPitch[MidiNoteNumber] = (pitch: MidiNoteNumber) => pitch

  implicit val octaveHasExactPitch: HasExactPitch[Octave] = (pitch: Octave) => MidiNoteNumber((pitch.value + 1) * 12)

  implicit val midiPitchHasExactPitch: HasExactPitch[MidiPitch] = (pitch: MidiPitch) => {
    octaveHasExactPitch.getMidiNoteNumber(pitch.octave) + pitch.pitchClass.value
  }

  implicit val scientificPitchHasExactPitch: HasExactPitch[ScientificPitch] = (pitch: ScientificPitch) => {
      val octaveValue = octaveHasExactPitch.getMidiNoteNumber(pitch.octave)
      val mvecValue = ScaleMath.OnAPiano.musicVectorToPitchClass(pitch.musicVector).value
      octaveValue + mvecValue
  }

}

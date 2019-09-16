package music.symbolic.properties

import music.symbolic.{MidiNoteNumber, MidiPitch, Octave, ScientificPitch}
import music.symbolic.tuning.TwelveToneEqualTemprament

object Pitch {

  trait HasExactPitch[A] {
    def getMidiNoteNumber(pitch: A): MidiNoteNumber
  }

  implicit val midiPitchNumberHasExactPitch: HasExactPitch[MidiNoteNumber] = (pitch: MidiNoteNumber) => pitch

  implicit val octaveHasExactPitch: HasExactPitch[Octave] = (pitch: Octave) => MidiNoteNumber((pitch.value + 1) * 12)

  implicit val midiPitchHasExactPitch: HasExactPitch[MidiPitch] = (pitch: MidiPitch) => {
    MidiNoteNumber(octaveHasExactPitch.getMidiNoteNumber(pitch.octave).value + pitch.pitchClass.value)
  }

  implicit val scientificPitchHasExactPitch: HasExactPitch[ScientificPitch] = (pitch: ScientificPitch) => {
      val octaveValue = octaveHasExactPitch.getMidiNoteNumber(pitch.octave).value
      val mvecValue = TwelveToneEqualTemprament.musicVectorToPitchClass(pitch.musicVector).value
      MidiNoteNumber(octaveValue + mvecValue)
  }

}

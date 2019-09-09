package music.properties

import music.{Scale, _}

object Pitch {

  trait HasExactPitch[A] {
    def getMidiNoteNumber: MidiNoteNumber
  }

  implicit class MidiPitchNumberHasExactPitch(midiNoteNumber: MidiNoteNumber) extends HasExactPitch[MidiNoteNumber] {
    override def getMidiNoteNumber: MidiNoteNumber = midiNoteNumber
  }

  implicit class OctaveHasExactPitch(octave: Octave) extends HasExactPitch[Octave] {
    override def getMidiNoteNumber: MidiNoteNumber = MidiNoteNumber((octave.value + 1) * 12)
  }

  implicit class MidiPitchHasExactPitch(midiPitch: MidiPitch) extends HasExactPitch[MidiPitch] {
    override def getMidiNoteNumber: MidiNoteNumber = midiPitch.octave.getMidiNoteNumber + midiPitch.pitchClass.value
  }

  implicit class ScientificPitchHasExactPitch(scientificPitch: ScientificPitch) extends HasExactPitch[ScientificPitch] {
    override def getMidiNoteNumber: MidiNoteNumber = {
      val octaveValue = scientificPitch.octave.getMidiNoteNumber
      val mvecValue = Scale.MAJOR_SCALE_MATH.musicVectorToPitchClass(scientificPitch.musicVector).value
      octaveValue + mvecValue
    }
  }

}

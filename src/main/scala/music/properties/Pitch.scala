package music.properties

import music.MidiPitch

object Pitch {

  type MidiPitchNumber = Int

  trait IsPitched[A] {
    def getMidiPitchNumber: MidiPitchNumber
  }

  implicit class MidiPitchIsPitched(midiPitch: MidiPitch) extends IsPitched[MidiPitch] {
    override def getMidiPitchNumber: MidiPitchNumber = midiPitch.getMidiPitchNumber
  }

}

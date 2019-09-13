package music.interpret

import music._

object NaivePitchSpelling extends PitchSpelling {

  override def interpret(midiPitches: Seq[MidiPitch]): Seq[ScientificPitch] = {
    midiPitches.map(interpretOne)
  }

  private def interpretOne(midiPitch: MidiPitch): ScientificPitch = {
    val vec = midiPitch.pitchClass.value match {
      case 0 => MusicVector(Step(0), Accidental(0))
      case 1 => MusicVector(Step(0), Accidental(1))
      case 2 => MusicVector(Step(1), Accidental(0))
      case 3 => MusicVector(Step(2), Accidental(-1))
      case 4 => MusicVector(Step(2), Accidental(0))
      case 5 => MusicVector(Step(3), Accidental(0))
      case 6 => MusicVector(Step(3), Accidental(1))
      case 7 => MusicVector(Step(4), Accidental(0))
      case 8 => MusicVector(Step(5), Accidental(-1))
      case 9 => MusicVector(Step(5), Accidental(0))
      case 10 => MusicVector(Step(6), Accidental(-1))
      case 11 => MusicVector(Step(6), Accidental(0))
    }
    ScientificPitch(midiPitch.octave, vec)
  }

}

package music.interpret

import music.{MidiPitch, ScientificPitch}

trait PitchSpelling {

  def interpret(midiPitches: Seq[MidiPitch]): Seq[ScientificPitch]

}

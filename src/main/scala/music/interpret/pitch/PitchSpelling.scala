package music.interpret.pitch

import music.symbolic.{MidiPitch, ScientificPitch}

trait PitchSpelling {

  def interpret(midiPitches: Seq[MidiPitch]): Seq[ScientificPitch]

}

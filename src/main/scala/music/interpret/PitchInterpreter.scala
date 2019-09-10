package music.interpret

import music.{MidiPitch, ScientificPitch}

trait PitchInterpreter {

  def interpret(midiPitches: Seq[MidiPitch]): Seq[ScientificPitch]

}

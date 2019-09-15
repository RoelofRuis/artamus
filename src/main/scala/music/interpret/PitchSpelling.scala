package music.interpret

import music.symbolic.{MidiPitch, ScientificPitch}

trait PitchSpelling {

  def interpret(midiPitches: Iterable[MidiPitch]): Iterable[ScientificPitch]

}

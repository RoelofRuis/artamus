package music.interpret

import music.{MidiPitch, ScientificPitch}

trait PitchSpelling {

  def interpret(midiPitches: Iterable[MidiPitch]): Iterable[ScientificPitch]

}

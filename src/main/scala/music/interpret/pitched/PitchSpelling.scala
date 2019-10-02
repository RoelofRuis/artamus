package music.interpret.pitched

import music.symbolic.pitch.{Octave, PitchClass, SpelledPitch}

trait PitchSpelling {

  def interpret(pitches: Seq[(Octave, PitchClass)]): Seq[(Octave, SpelledPitch)]

}

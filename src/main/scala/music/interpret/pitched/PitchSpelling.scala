package music.interpret.pitched

import music.symbolic.pitched.{Pitch, PitchClass, SpelledPitch}

trait PitchSpelling {

  def interpret(pitches: Seq[Pitch[PitchClass]]): Seq[Pitch[SpelledPitch]]

}

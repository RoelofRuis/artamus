package music.interpret.pitch

import music.symbolic.pitched.{Pitch, PitchClass, Spelled}

trait PitchSpelling {

  def interpret(pitches: Seq[Pitch[PitchClass]]): Seq[Pitch[Spelled]]

}

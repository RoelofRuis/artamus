package music.interpret.pitch

import music.symbolic.Pitched.{Pitch, PitchClass, Spelled}

trait PitchSpelling {

  def interpret(pitches: Seq[Pitch[PitchClass]]): Seq[Pitch[Spelled]]

}

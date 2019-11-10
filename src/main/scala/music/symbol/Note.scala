package music.symbol

import music.primitives.{Octave, PitchClass, ScientificPitch}

final case class Note(
  octave: Octave,
  pitchClass: PitchClass,
  scientificPitch: Option[ScientificPitch]
) extends SymbolType {

  def withScientificPitch(pitch: ScientificPitch): Note = this.copy(scientificPitch = Some(pitch))

}

object Note {

  def apply(
    octave: Octave,
    pitchClass: PitchClass,
  ): Note = Note(octave, pitchClass, None)

}

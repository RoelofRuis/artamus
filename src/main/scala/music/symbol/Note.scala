package music.symbol

import music.primitives.{Duration, Octave, PitchClass, ScientificPitch}

final case class Note(
  octave: Octave,
  pitchClass: PitchClass,
  duration: Duration,
  scientificPitch: Option[ScientificPitch]
) extends SymbolType {

  override def getDuration: Duration = duration

  def withScientificPitch(pitch: ScientificPitch): Note = this.copy(scientificPitch = Some(pitch))

}

object Note {

  def apply(
    octave: Octave,
    pitchClass: PitchClass,
    duration: Duration
  ): Note = Note(octave, pitchClass, duration, None)

}

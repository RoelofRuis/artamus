package music.symbols

import music.primitives.{Duration, Octave, PitchClass}

final case class Note(
  octave: Octave,
  pitchClass: PitchClass,
  duration: Duration
) extends SymbolType

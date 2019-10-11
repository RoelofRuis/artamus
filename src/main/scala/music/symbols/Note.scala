package music.symbols

import music.collection.SymbolProperties
import music.primitives.{Duration, Octave, PitchClass}

object Note extends SymbolType {

  def apply(
    octave: Octave,
    pitchClass: PitchClass,
    duration: Duration
  ): SymbolProperties[Note.type] =
    SymbolProperties[Note.type]
      .add(octave)
      .add(pitchClass)
      .add(duration)

}

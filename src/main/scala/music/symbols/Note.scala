package music.symbols

import music.collection.SymbolProperties
import music.primitives.{Duration, Octave, PitchClass}

trait Note extends SymbolType

object Note {

  def apply(
    octave: Octave,
    pitchClass: PitchClass,
    duration: Duration
  ): SymbolProperties[Note] =
    SymbolProperties[Note]
      .add(octave)
      .add(pitchClass)
      .add(duration)

}

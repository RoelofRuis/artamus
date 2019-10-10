package music.symbolic

import music.symbolic.Symbols.{Chord, MetaSymbol, Note, SymbolType}
import music.symbolic.pitch.{ChordFunctions, ChordRoot, Octave, PitchClass}
import music.symbolic.symbol.{Key, TimeSignature}
import music.symbolic.temporal.Duration

object Properties {

  trait Property[S <: SymbolType, P]

  implicit val noteHasPitchClass: Property[Note.type, PitchClass] = new Property[Note.type, PitchClass] {}
  implicit val noteHasOctave: Property[Note.type, Octave] = new Property[Note.type, Octave] {}
  implicit val noteHasDuration: Property[Note.type, Duration] = new Property[Note.type, Duration] {}

  implicit val chordHasRoot: Property[Chord.type, ChordRoot] = new Property[Chord.type, ChordRoot] {}
  implicit val chordHasFunctions: Property[Chord.type, ChordFunctions] = new Property[Chord.type, ChordFunctions] {}

  implicit val metaSymbolHasTimeSignature: Property[MetaSymbol.type, TimeSignature] = new Property[MetaSymbol.type, TimeSignature] {}
  implicit val metaSymbolHasKey: Property[MetaSymbol.type, Key] = new Property[MetaSymbol.type, Key] {}

}

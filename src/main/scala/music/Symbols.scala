package music

import music.primitives._

object Symbols {

  trait SymbolType
  trait Property[S <: SymbolType, P]

  case object Note extends SymbolType
  implicit val noteHasPitchClass: Property[Note.type, PitchClass] = new Property[Note.type, PitchClass] {}
  implicit val noteHasOctave: Property[Note.type, Octave] = new Property[Note.type, Octave] {}
  implicit val noteHasDuration: Property[Note.type, Duration] = new Property[Note.type, Duration] {}

  case object Chord extends SymbolType
  implicit val chordHasRoot: Property[Chord.type, ChordRoot] = new Property[Chord.type, ChordRoot] {}
  implicit val chordHasFunctions: Property[Chord.type, ChordFunctions] = new Property[Chord.type, ChordFunctions] {}

  case object MetaSymbol extends SymbolType
  implicit val metaSymbolHasTimeSignature: Property[MetaSymbol.type, TimeSignature] = new Property[MetaSymbol.type, TimeSignature] {}
  implicit val metaSymbolHasKey: Property[MetaSymbol.type, Key] = new Property[MetaSymbol.type, Key] {}

}

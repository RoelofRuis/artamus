package music

import music.primitives._

object Symbols {

  sealed trait SymbolType

  final case class Property[S <: SymbolType, P] private ()
  object Property {
    def apply[S <: SymbolType, P]: Property[S, P] = new Property[S, P]()
  }

  case object Note extends SymbolType
  implicit val noteHasPitchClass: Property[Note.type, PitchClass] = Property[Note.type, PitchClass]
  implicit val noteHasOctave: Property[Note.type, Octave] = Property[Note.type, Octave]
  implicit val noteHasDuration: Property[Note.type, Duration] = Property[Note.type, Duration]

  case object Chord extends SymbolType
  implicit val chordHasRoot: Property[Chord.type, ChordRoot] = Property[Chord.type, ChordRoot]
  implicit val chordHasFunctions: Property[Chord.type, ChordFunctions] = Property[Chord.type, ChordFunctions]

  case object MetaSymbol extends SymbolType
  implicit val metaSymbolHasTimeSignature: Property[MetaSymbol.type, TimeSignature] = Property[MetaSymbol.type, TimeSignature]
  implicit val metaSymbolHasKey: Property[MetaSymbol.type, Key] = Property[MetaSymbol.type, Key]

}

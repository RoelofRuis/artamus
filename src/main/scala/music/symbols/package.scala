package music

import music.primitives._

package object symbols {

  trait SymbolType

  final case class Property[S <: SymbolType, P] private ()
  object Property {
    def apply[S <: SymbolType, P]: Property[S, P] = new Property[S, P]()
  }

  implicit val noteHasPitchClass: Property[Note.type, PitchClass] = Property[Note.type, PitchClass]
  implicit val noteHasOctave: Property[Note.type, Octave] = Property[Note.type, Octave]
  implicit val noteHasDuration: Property[Note.type, Duration] = Property[Note.type, Duration]

  implicit val chordHasRoot: Property[Chord.type, ChordRoot] = Property[Chord.type, ChordRoot]
  implicit val chordHasFunctions: Property[Chord.type, ChordFunctions] = Property[Chord.type, ChordFunctions]
  implicit val chordHasDuration: Property[Chord.type, Duration] = Property[Chord.type, Duration]

  implicit val metaSymbolHasTimeSignature: Property[MetaSymbol.type, TimeSignature] = Property[MetaSymbol.type, TimeSignature]
  implicit val metaSymbolHasKey: Property[MetaSymbol.type, Key] = Property[MetaSymbol.type, Key]

}

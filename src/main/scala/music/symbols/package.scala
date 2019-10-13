package music

import music.primitives._
import music.spelling.SpelledPitch

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

  implicit val keyHasRoot: Property[Key.type, SpelledPitch] = Property[Key.type, SpelledPitch]
  implicit val keyHasScale: Property[Key.type, Scale] = Property[Key.type, Scale]

  implicit val timeSignatureHasDivisino: Property[TimeSignature.type, TimeSignatureDivision] = Property[TimeSignature.type, TimeSignatureDivision]

}

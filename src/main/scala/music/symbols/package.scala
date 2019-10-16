package music

import music.primitives._
import music.spelling.SpelledPitch

package object symbols {

  trait SymbolType

  final case class Property[S <: SymbolType, P] private ()
  object Property {
    def apply[S <: SymbolType, P]: Property[S, P] = new Property[S, P]()
  }

  implicit val noteHasPitchClass: Property[Note, PitchClass] = Property[Note, PitchClass]
  implicit val noteHasOctave: Property[Note, Octave] = Property[Note, Octave]
  implicit val noteHasDuration: Property[Note, Duration] = Property[Note, Duration]

  implicit val chordHasRoot: Property[Chord, ChordRoot] = Property[Chord, ChordRoot]
  implicit val chordHasFunctions: Property[Chord, ChordFunctions] = Property[Chord, ChordFunctions]
  implicit val chordHasDuration: Property[Chord, Duration] = Property[Chord, Duration]

  implicit val keyHasRoot: Property[Key, SpelledPitch] = Property[Key, SpelledPitch]
  implicit val keyHasScale: Property[Key, Scale] = Property[Key, Scale]

  implicit val timeSignatureHasDivisino: Property[TimeSignature, TimeSignatureDivision] = Property[TimeSignature, TimeSignatureDivision]

}

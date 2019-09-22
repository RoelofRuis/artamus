package music.symbolic.properties

import music.symbolic._
import music.symbolic.pitched.PitchClass

object Symbols {

  trait Symbol[A]
  trait StackableSymbol[A] extends Symbol[A]

  implicit object TimeSignatureSymbol extends Symbol[TimeSignature]
  implicit object KeySymbol extends Symbol[Key]

  implicit object NoteSymbol extends StackableSymbol[Note[PitchClass]]

}

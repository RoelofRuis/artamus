package music.symbolic.properties

import music.symbolic.Pitched.PitchClass
import music.symbolic._

object Symbols {

  trait Symbol[A]
  trait StackableSymbol[A] extends Symbol[A]

  implicit object TimeSignatureSymbol extends Symbol[TimeSignature]
  implicit object KeySymbol extends Symbol[Key]

  implicit object NoteSymbol extends StackableSymbol[Note[PitchClass]]

}

package nl.roelofruis.artamus.core.layout

import nl.roelofruis.artamus.core.common.Maths._
import nl.roelofruis.artamus.core.track.Pitched.{Key, Octave, PitchDescriptor}

sealed trait StaffGlyph

object StaffGlyph {

  final case class NoteGroupGlyph(
    notes: Seq[(PitchDescriptor, Octave)]
  ) extends StaffGlyph {
    def isEmpty: Boolean = notes.isEmpty
    def isChord: Boolean = notes.size > 1
  }

  final case class RestGlyph() extends StaffGlyph

  final case class KeyGlyph(
    key: Key,
  ) extends StaffGlyph

  final case class TimeSignatureGlyph(
    num: Int,
    denom: FractionalPowerOfTwo
  ) extends StaffGlyph

}
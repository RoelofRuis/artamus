package nl.roelofruis.artamus.core.layout

import nl.roelofruis.artamus.core.common.Maths._
import nl.roelofruis.artamus.core.track.Pitched.{Octave, PitchDescriptor, Scale}

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
    root: PitchDescriptor,
    scale: Scale
  ) extends StaffGlyph

  final case class TimeSignatureGlyph(
    num: Int,
    denom: FractionalPowerOfTwo
  ) extends StaffGlyph

}
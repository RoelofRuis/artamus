package domain.display.staff

import domain.primitives.{NoteValue, PitchSpelling, Scale, ScientificPitch, TimeSignatureDivision}

sealed trait StaffGlyph

object StaffGlyph {

  final case class NoteGroupGlyph(
    duration: NoteValue,
    notes: Seq[ScientificPitch],
    tieToNext: Boolean
  ) extends StaffGlyph {
    def isEmpty: Boolean = notes.isEmpty
    def isChord: Boolean = notes.size > 1
  }

  final case class RestGlyph(
    duration: NoteValue,
    silent: Boolean
  ) extends StaffGlyph

  final case class FullBarRestGlyph(
    numberOfBars: Int
  ) extends StaffGlyph

  final case class KeyGlyph(
    root: PitchSpelling,
    scale: Scale
  ) extends StaffGlyph

  final case class TimeSignatureGlyph(
    division: TimeSignatureDivision
  ) extends StaffGlyph

}
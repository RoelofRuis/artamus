package music.display.neww.staff

import music.primitives.{NoteValue, PitchSpelling, Scale, ScientificPitch, TimeSignatureDivision}

sealed trait StaffGlyph

object StaffGlyph {

  final case class NoteGroupGlyph(
    duration: NoteValue,
    notes: Seq[ScientificPitch],
    tieToNext: Boolean
  ) extends StaffGlyph

  final case class RestGlyph(
    duration: NoteValue,
    silent: Boolean
  ) extends StaffGlyph

  final case class KeyGlyph(
    root: PitchSpelling,
    scale: Scale
  ) extends StaffGlyph

  final case class TimeSignatureGlyph(
    division: TimeSignatureDivision
  ) extends StaffGlyph

}
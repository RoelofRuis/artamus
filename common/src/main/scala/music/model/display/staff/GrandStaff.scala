package music.model.display.staff

import music.model.display.Staff

final case class GrandStaff(
  upper: NoteStaff,
  lower: NoteStaff
) extends Staff

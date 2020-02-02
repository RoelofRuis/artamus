package domain.display.staff

import domain.display.Staff

final case class GrandStaff(
  upper: NoteStaff,
  lower: NoteStaff
) extends Staff

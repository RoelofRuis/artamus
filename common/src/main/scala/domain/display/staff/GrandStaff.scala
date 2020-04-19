package domain.display.staff

final case class GrandStaff(
  upper: NoteStaff,
  lower: NoteStaff
) extends Staff

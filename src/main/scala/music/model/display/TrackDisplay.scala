package music.model.display

import music.model.display.chord.ChordStaff
import music.model.display.staff.Staff

final case class TrackDisplay(
  upper: Staff,
  lower: Staff,
  chords: ChordStaff
)

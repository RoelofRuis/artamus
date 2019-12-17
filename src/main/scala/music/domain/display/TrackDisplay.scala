package music.domain.display

import music.domain.display.chord.ChordStaff
import music.domain.display.staff.Staff

final case class TrackDisplay(
  staff: Staff,
  chordStaff: ChordStaff
)

package music.domain.display

import music.domain.display.chord.ChordStaff
import music.domain.display.staff.Staff

final case class DisplayTrack(
  staff: Staff,
  chordStaff: ChordStaff
)

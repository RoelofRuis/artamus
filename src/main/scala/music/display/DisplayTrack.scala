package music.display

import music.display.chord.ChordStaff
import music.display.staff.Staff

final case class DisplayTrack(
  staff: Staff,
  chordStaff: ChordStaff
)

package music.display.neww

import music.display.neww.chord.ChordStaff
import music.display.neww.staff.Staff

final case class DisplayTrack(
  staff: Staff,
  chordStaff: ChordStaff
)

package music.model.display

import music.model.write.Track

object Display {

  import music.model.display.chord.ChordDisplay._
  import music.model.display.staff.StaffDisplay._

  def displayTrack(track: Track): TrackDisplay = {
    val noteStaffGroup = track.getNotes
    val chordStaffGroup = track.getChords

    TrackDisplay(noteStaffGroup + chordStaffGroup)
  }


}

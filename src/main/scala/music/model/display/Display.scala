package music.model.display

import music.model.write.track.Track

object Display {

  import music.model.display.chord.ChordDisplay._
  import music.model.display.staff.StaffDisplay._

  def displayTrack(track: Track): TrackDisplay = {
    val staves = track.getStaves

    TrackDisplay(
      staves._1,
      staves._2,
      track.getChordStaff
    )
  }


}

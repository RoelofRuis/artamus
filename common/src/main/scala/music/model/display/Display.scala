package music.model.display

import music.model.write.Layers.{ChordLayer, NoteLayer, RhythmLayer}
import music.model.write.{Keys, Track}

object Display {

  import music.model.display.chord.ChordDisplay._
  import music.model.display.staff.StaffDisplay._

  def displayTrack(track: Track): TrackDisplay = {
    val staffGroups = track.layers.map {
      case l: ChordLayer => l.getChords
      case l: NoteLayer => StaffDisplayable(l.timeSignatures, l.keys, l.notes).getNotes
      case l: RhythmLayer => StaffDisplayable(l.timeSignatures, Keys.apply(), l.notes).getRhythm
    }.foldRight(StaffGroup.empty)(_ + _)

    TrackDisplay(staffGroups)
  }


}

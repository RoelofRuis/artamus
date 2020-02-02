package domain.display

import domain.write.Layers.{ChordLayer, NoteLayer, RhythmLayer}
import domain.write.{Keys, Track}

object Display {

  import domain.display.chord.ChordDisplay._
  import domain.display.staff.StaffDisplay._

  def displayTrack(track: Track): TrackDisplay = {
    val staffGroups = track.layers.map {
      case l: ChordLayer => l.getChords
      case l: NoteLayer => StaffDisplayable(l.timeSignatures, l.keys, l.notes).getNotes
      case l: RhythmLayer => StaffDisplayable(l.timeSignatures, Keys.apply(), l.notes).getRhythm
    }.foldRight(StaffGroup.empty)(_ + _)

    TrackDisplay(staffGroups)
  }


}

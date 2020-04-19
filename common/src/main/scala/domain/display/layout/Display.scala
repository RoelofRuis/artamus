package domain.display.layout

import domain.display.TrackDisplay
import domain.display.staff.StaffGroup
import domain.write.Track
import domain.write.layers.{ChordLayer, NoteLayer, RhythmLayer}

object Display {

  import domain.display.layout.DisplayableLayers._

  def displayTrack(track: Track): TrackDisplay = {
    val staffGroups = track
      .layers
      .map { case (_, layer) => layer }
      .filter(_.visible)
      .map(_.data)
      .map {
        case l: ChordLayer => l.display
        case l: NoteLayer => l.display
        case l: RhythmLayer => l.display
      }
      .foldRight(StaffGroup.empty)(_ + _)

    TrackDisplay(staffGroups)
  }
}

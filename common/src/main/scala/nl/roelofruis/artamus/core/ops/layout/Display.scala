package nl.roelofruis.artamus.core.ops.layout

import nl.roelofruis.artamus.core.model.display.TrackDisplay
import nl.roelofruis.artamus.core.model.display.staff.StaffGroup
import nl.roelofruis.artamus.core.model.write.Track
import nl.roelofruis.artamus.core.model.write.layers.{ChordLayer, NoteLayer, RhythmLayer}

object Display {

  import nl.roelofruis.artamus.core.ops.layout.DisplayableLayers._

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

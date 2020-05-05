package nl.roelofruis.artamus.core.ops.layout

import nl.roelofruis.artamus.core.model.display.Display
import nl.roelofruis.artamus.core.model.display.staff.StaffGroup
import nl.roelofruis.artamus.core.model.track.Track
import nl.roelofruis.artamus.core.model.track.layers.{ChordLayer, NoteLayer, RhythmLayer}

object Layout {

  import nl.roelofruis.artamus.core.ops.layout.DisplayableLayers._

  def calculateDisplay(track: Track): Display = {
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

    Display(staffGroups)
  }
}

package nl.roelofruis.artamus.core.layout

import nl.roelofruis.artamus.core.layout.Staff.StaffGroup
import nl.roelofruis.artamus.core.track.Layer.{ChordLayer, NoteLayer}
import nl.roelofruis.artamus.core.track.Track

final case class DisplayableMusic(staffGroup: StaffGroup)

object DisplayableMusic {

  import nl.roelofruis.artamus.core.layout.algorithms.DisplayableLayers._

  def fromTrack(track: Track): DisplayableMusic = {
    val staffGroup = track.layers
      .map {
        case l: ChordLayer => l.display
        case l: NoteLayer => l.display
      }
      .foldRight(Seq[Staff]())(_ ++ _)

    DisplayableMusic(staffGroup)
  }

}
package nl.roelofruis.artamus.core.layout

import nl.roelofruis.artamus.core.layout.Staff.StaffGroup
import nl.roelofruis.artamus.core.layout.algorithms.DisplayableLayers
import nl.roelofruis.artamus.core.track.Layer.{ChordLayer, NoteLayer}
import nl.roelofruis.artamus.core.track.Track

final case class DisplayableMusic(staffGroup: StaffGroup)

object DisplayableMusic {

  def fromTrack(track: Track): DisplayableMusic = {
    val staffGroup = track.layers
      .map {
        case l: ChordLayer => DisplayableLayers.displayChordLayer(l, track.metres)
        case l: NoteLayer => DisplayableLayers.displayNoteLayer(l, track.metres)
      }
      .foldRight(Seq[Staff]())(_ ++ _)

    DisplayableMusic(staffGroup)
  }

}
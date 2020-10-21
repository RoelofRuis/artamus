package nl.roelofruis.artamus.core.layout

import nl.roelofruis.artamus.core.layout.Staff.StaffGroup
import nl.roelofruis.artamus.core.layout.algorithms.DisplayableLayers
import nl.roelofruis.artamus.core.track.Track
import nl.roelofruis.artamus.core.track.Track.{ChordLayer, NoteLayer, RNALayer}

final case class DisplayableMusic(staffGroup: StaffGroup)

object DisplayableMusic {

  def fromTrack(track: Track): DisplayableMusic = {
    val staffGroup = track.layers
      .map {
        case l: ChordLayer => DisplayableLayers.displayChordLayer(track, l)
        case l: NoteLayer => DisplayableLayers.displayNoteLayer(track, l)
        case l: RNALayer => DisplayableLayers.displayRNALayer(track, l)
        case _ => Seq()
      }
      .foldRight(Seq[Staff]())(_ ++ _)

    DisplayableMusic(staffGroup)
  }

}
package nl.roelofruis.artamus.core.track.algorithms.voicing

import nl.roelofruis.artamus.application.Model.Settings
import nl.roelofruis.artamus.core.common.Containers.Windowed
import nl.roelofruis.artamus.core.track.Pitched._
import nl.roelofruis.artamus.core.track.algorithms.TunedMaths

case class ChordVoicer(settings: Settings) extends TunedMaths {

  def voiceChords(chordTrack: ChordTrack): NoteTrack = {
    chordTrack.map { case Windowed(window, element) =>
      val noteGroup: NoteGroup = element
        .quality
        .intervals
        .map(intervalDescriptor => Note(element.root + intervalDescriptor, 4))

      Windowed(window, noteGroup)
    }
  }

}

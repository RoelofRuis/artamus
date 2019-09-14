package server.domain.track

import javax.inject.Inject
import protocol.Query
import pubsub.Dispatcher

private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Query],
  state: TrackState
) {

  import music.properties.Pitch.midiPitchHasExactPitch
  import music.properties.Symbols._

  dispatcher.subscribe[GetMidiPitches.type]{ _ =>
    state.getTrack.getAllStackedSymbols.map {
      case (_, notes) => midiPitchHasExactPitch.getMidiNoteNumber(notes.head.pitch).value
    }.toList
  }

}

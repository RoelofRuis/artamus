package server.domain.track

import javax.inject.Inject
import music.Note
import protocol.Query
import protocol.ServerInterface.Dispatcher

private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Query],
  state: TrackState
) {

  import TrackSymbols._

  dispatcher.subscribe[GetTrackMidiNotes.type]{ _ =>
    state.getTrack.getSymbols[Note]
      .map(_.midiPitch.toMidiPitchNumber)
      .toList
  }

}

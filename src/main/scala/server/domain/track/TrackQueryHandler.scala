package server.domain.track

import javax.inject.Inject
import music.Note
import protocol.{Dispatcher, Query}

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

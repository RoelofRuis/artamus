package server.domain.track

import javax.inject.Inject
import music.{Duration, MidiPitch}
import protocol.Query
import protocol.ServerInterface.Dispatcher

private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Query],
  state: TrackState
) {

  import server.model.TrackSymbols._

  dispatcher.subscribe[GetTrackMidiNotes.type]{ _ =>
    state.getTrack.getSymbols[(Duration, MidiPitch)]
      .map(_._2.toMidiPitchNumber)
      .toList
  }

}

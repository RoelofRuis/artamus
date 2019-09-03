package server.domain.track

import javax.inject.Inject
import music.{Duration, MidiPitch}
import server.dispatchers.QueryDispatcherImpl
import server.dispatchers.QueryDispatcherImpl.QueryHandler

private[server] class TrackQueryHandler @Inject() (
  dispatcher: QueryDispatcherImpl,
  state: TrackState
) {

  import server.model.TrackSymbols._

  dispatcher.subscribe(QueryHandler[GetTrackMidiNotes.type]{ _ =>
    state.getTrack.getSymbols[(Duration, MidiPitch)]
      .map(_._2.toMidiPitchNumber)
      .toList
  })

}

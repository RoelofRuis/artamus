package server.domain.track

import javax.inject.Inject
import server.api.Track.GetTrackMidiNotes
import server.dispatchers.QueryDispatcherImpl
import server.dispatchers.QueryDispatcherImpl.QueryHandler

private[server] class TrackQueryHandler @Inject() (
  dispatcher: QueryDispatcherImpl,
  state: TrackState
) {

  dispatcher.subscribe(QueryHandler[GetTrackMidiNotes.type]{ _ =>
    state.midiNoteList()
  })

}

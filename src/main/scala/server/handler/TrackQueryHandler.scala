package server.handler

import javax.inject.Inject
import server.api.Track.GetTrackMidiNotes
import server.domain.TrackState

private[server] class TrackQueryHandler @Inject() (
  dispatcher: QueryDispatcherImpl,
  state: TrackState
) {

  dispatcher.subscribe(QueryHandler[GetTrackMidiNotes.type]{ _ =>
    state.midiNoteList
  })

}

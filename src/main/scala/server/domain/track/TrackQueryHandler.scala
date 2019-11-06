package server.domain.track

import javax.inject.Inject
import music.symbol.{Chord, Note}
import protocol.Query
import pubsub.Dispatcher

private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Query],
  state: TrackState
) {

  dispatcher.subscribe[GetNotes.type]{ _ =>
    state
      .readState
      .iterate[Note]
      .toSeq
  }

  dispatcher.subscribe[GetChords.type]{ _ =>
    state
      .readState
      .iterate[Chord]
      .toSeq
  }

}

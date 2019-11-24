package server.domain.track

import javax.inject.Inject
import music.domain.track.symbol.{Chord, Note}
import music.math.temporal.Position
import protocol.Query
import pubsub.Dispatcher
import server.Request

private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Request, Query]
) {

  dispatcher.subscribe[ReadNotes.type]{ req =>
    req
      .user
      .workspace
      .getEditedTrack
      .read[Note]()
      .toSeq
  }

  dispatcher.subscribe[ReadChords.type]{ req =>
    req
      .user
      .workspace
      .getEditedTrack
      .read[Chord]()
      .toSeq
  }

  dispatcher.subscribe[ReadMidiNotes.type]{ req =>
    import music.playback._

    req
      .user
      .workspace
      .getEditedTrack
      .iterate(Position.ZERO)
      .toSeq
  }

}

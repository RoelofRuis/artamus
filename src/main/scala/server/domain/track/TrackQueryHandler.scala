package server.domain.track

import javax.inject.Inject
import music.domain.track.symbol.{Chord, Note}
import music.math.temporal.Position
import protocol.Query
import pubsub.Dispatcher

private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Query],
  savepoint: Savepoint
) {

  dispatcher.subscribeRequest[ReadNotes.type]{ _ =>
    savepoint
      .getCurrentTrack
      .read[Note]()
      .toSeq
  }

  dispatcher.subscribeRequest[ReadChords.type]{ _ =>
    savepoint
      .getCurrentTrack
      .read[Chord]()
      .toSeq
  }

  dispatcher.subscribeRequest[ReadMidiNotes.type]{ _ =>
    import music.playback._

    savepoint.getCurrentTrack
      .iterate(Position.ZERO)
      .toSeq
  }

}

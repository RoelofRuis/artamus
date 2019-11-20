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

  type Action[A <: { type Res }] = A => A#Res

  val readNotes: Action[ReadNotes.type] = { _: ReadNotes.type =>
    savepoint
      .getCurrentTrack
      .read[Note]()
      .toSeq
  }

  val readChords: Action[ReadChords.type] = { _: ReadChords.type =>
    savepoint
      .getCurrentTrack
      .read[Chord]()
      .toSeq
  }

  val readMidiNotes: Action[ReadMidiNotes.type] = { _: ReadMidiNotes.type =>
    import music.playback._

    savepoint.getCurrentTrack
      .iterate(Position.ZERO)
      .toSeq
  }

  dispatcher.subscribe(readNotes)
  dispatcher.subscribe(readChords)
  dispatcher.subscribe(readMidiNotes)

}

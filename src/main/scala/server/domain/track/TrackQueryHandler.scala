package server.domain.track

import javax.inject.Inject
import music.math.temporal.Position
import music.playback.MidiNoteIterator
import music.symbol.{Chord, Note}
import protocol.Query
import pubsub.Dispatcher

private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Query],
  savepoint: Savepoint
) {

  dispatcher.subscribe[ReadNotes.type]{ _ =>
    savepoint
      .getCurrentTrack
      .read[Note]()
      .toSeq
  }

  dispatcher.subscribe[ReadChords.type]{ _ =>
    savepoint
      .getCurrentTrack
      .read[Chord]()
      .toSeq
  }

  dispatcher.subscribe[ReadMidiNotes.type]{ _ =>
    new MidiNoteIterator(savepoint.getCurrentTrack)
      .iterate(Position.ZERO)
      .toSeq
  }

}

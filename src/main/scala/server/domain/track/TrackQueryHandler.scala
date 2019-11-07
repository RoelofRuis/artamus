package server.domain.track

import javax.inject.Inject
import music.playback.MidiNoteIterator
import music.primitives.Position
import music.symbol.{Chord, Note}
import protocol.Query
import pubsub.Dispatcher

private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Query],
  state: TrackState
) {

  dispatcher.subscribe[ReadNotes.type]{ _ =>
    state
      .readState
      .read[Note]()
      .toSeq
  }

  dispatcher.subscribe[ReadChords.type]{ _ =>
    state
      .readState
      .read[Chord]()
      .toSeq
  }

  dispatcher.subscribe[ReadMidiNotes.type]{ _ =>
    new MidiNoteIterator(state.readState)
      .iterate(Position.zero)
      .toSeq
  }

}

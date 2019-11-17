package server.domain.track

import javax.inject.Inject
import music.math.temporal.Position
import music.playback.MidiNoteIterator
import music.symbol.{Chord, Note}
import protocol.Query
import pubsub.Dispatcher

private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Query],
  state: TrackState
) {

  dispatcher.subscribe[ReadNotes.type]{ _ =>
    state
      .getEditable
      .read[Note]()
      .toSeq
  }

  dispatcher.subscribe[ReadChords.type]{ _ =>
    state
      .getEditable
      .read[Chord]()
      .toSeq
  }

  dispatcher.subscribe[ReadMidiNotes.type]{ _ =>
    new MidiNoteIterator(state.getEditable)
      .iterate(Position.ZERO)
      .toSeq
  }

}

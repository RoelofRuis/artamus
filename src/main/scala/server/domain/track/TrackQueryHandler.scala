package server.domain.track

import javax.inject.Inject
import music.primitives.MidiNoteNumber
import music.symbols.{Chord, Note}
import protocol.Query
import pubsub.Dispatcher

private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Query],
  state: TrackState
) {

  import music.analysis.TwelveToneEqualTemprament._

  dispatcher.subscribe[GetNotes.type]{ _ =>
    state
      .readState
      .getSymbolTrack[Note]
      .readAll
  }

  dispatcher.subscribe[GetChords.type]{ _ =>
    state
      .readState
      .getSymbolTrack[Chord]
      .readAll
  }

  dispatcher.subscribe[GetMidiPitches.type]{ _ =>
    state
      .readState
      .getSymbolTrack[Note]
      .readAllGrouped.map {
        _.flatMap { note =>
          val pc = note.symbol.pitchClass
          val oct = note.symbol.octave
          Some(MidiNoteNumber(oct, pc).value)
        }.toList
    }.toList
  }

}

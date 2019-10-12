package server.domain.track

import javax.inject.Inject
import music.primitives.{Octave, PitchClass}
import music.symbols.{Chord, MetaSymbol, Note}
import protocol.Query
import pubsub.Dispatcher

private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Query],
  state: TrackState
) {

  import music.analysis.TwelveToneEqualTemprament._

  dispatcher.subscribe[GetMeta.type ]{ _ =>
    state
      .readState
      .getSymbolTrack[MetaSymbol.type]
      .readAll
  }

  dispatcher.subscribe[GetNotes.type]{ _ =>
    state
      .readState
      .getSymbolTrack[Note.type]
      .readAll
  }

  dispatcher.subscribe[GetChords.type]{ _ =>
    state
      .readState
      .getSymbolTrack[Chord.type]
      .readAll
  }

  dispatcher.subscribe[GetMidiPitches.type]{ _ =>
    state
      .readState
      .getSymbolTrack[Note.type]
      .readAllWithPosition.map {
      case (_, notes) =>
        notes
          .flatMap { symbol =>
            val pc = symbol.get[PitchClass]
            val oct = symbol.get[Octave]
            if (pc.isDefined && oct.isDefined) Some(tuning.octAndPcToNoteNumber(oct.get, pc.get).value)
            else None
          }
        .toList
    }.toList
  }

}

package server.domain.track

import javax.inject.Inject
import music.symbolic.pitch.{Octave, Pitch, PitchClass}
import protocol.Query
import pubsub.Dispatcher

private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Query],
  state: TrackState
) {

  import server.analysis.Properties._

  import music.analysis.TwelveToneEqualTemprament._

  dispatcher.subscribe[GetMidiPitches.type]{ _ =>
    state.readState.readAllWithPosition.map {
      case (_, notes) =>
        notes
          .flatMap { symbol =>
            val pc = symbol.props.get[PitchClass]
            val oct = symbol.props.get[Octave]
            if (pc.isDefined && oct.isDefined) Some(tuning.pitchToNoteNumber(Pitch(oct.get, pc.get)).value)
            else None
          }
        .toList
    }.toList
  }

}

package server.domain.track

import javax.inject.Inject
import music.symbolic.pitch.{Octave, Pitch, PitchClass}
import protocol.Query
import pubsub.Dispatcher

private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Query],
  state: TrackState
) {

  import music.interpret.pitched.TwelveToneEqualTemprament._

  dispatcher.subscribe[GetMidiPitches.type]{ _ =>
    state.readState.readAllWithPosition.map {
      case (_, notes) =>
        notes
          .flatMap { in =>
            val pc = in.getProperty[PitchClass]
            val oct = in.getProperty[Octave]
            if (pc.isDefined && oct.isDefined) Some(tuning.pitchToNoteNumber(Pitch(oct.get, pc.get)).value)
            else None
          }
        .toList
    }.toList
  }

}

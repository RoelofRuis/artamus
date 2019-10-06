package server.domain.track

import blackboard.TrackSymbol
import javax.inject.Inject
import music.symbolic.temporal.{Duration, Position}
import protocol.Command
import pubsub.Dispatcher

private[server] class TrackCommandHandler @Inject() (
  dispatcher: Dispatcher[Command],
  state: TrackState
) {

  import server.analysis.Properties._

  dispatcher.subscribe[NewTrack.type]{ _ =>
    state.reset()
    true
  }

  dispatcher.subscribe[SetTimeSignature]{ command =>
    // TODO: move explicit position away from here
    state.setSymbol(Position(Duration.QUARTER, 0), TrackSymbol.empty.addProperty(command.t))
    true
  }

  dispatcher.subscribe[SetKey] { command =>
    // TODO: move explicit position away from here
    state.setSymbol(Position(Duration.QUARTER, 0), TrackSymbol.empty.addProperty(command.k))
    true
  }

  dispatcher.subscribe[AddNote] { command =>
    state.addSymbol(
      command.position,
      TrackSymbol
        .empty
        .addProperty(command.pitch.octave)
        .addProperty(command.pitch.pitchClass)
        .addProperty(command.duration)
    )
    true
  }

}

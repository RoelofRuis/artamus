package server.domain.track

import javax.inject.Inject
import music.symbolic.temporal.{Duration, Position}
import protocol.Command
import pubsub.Dispatcher
import server.domain.track.container.SymbolProperties

private[server] class TrackCommandHandler @Inject() (
  dispatcher: Dispatcher[Command],
  state: TrackState
) {

  dispatcher.subscribe[NewTrack.type]{ _ =>
    state.reset()
    true
  }

  dispatcher.subscribe[SetTimeSignature]{ command =>
    // TODO: move explicit position away from here
    state.setSymbol(Position(Duration.QUARTER, 0), SymbolProperties.empty.add(command.t))
    true
  }

  dispatcher.subscribe[SetKey] { command =>
    // TODO: move explicit position away from here
    state.setSymbol(Position(Duration.QUARTER, 0), SymbolProperties.empty.add(command.k))
    true
  }

  dispatcher.subscribe[AddNote] { command =>
    state.addSymbol(
      command.position,
      SymbolProperties
        .empty
        .add(command.pitch.octave)
        .add(command.pitch.pitchClass)
        .add(command.duration)
    )
    true
  }

}

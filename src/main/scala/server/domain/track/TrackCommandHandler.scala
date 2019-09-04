package server.domain.track

import javax.inject.Inject
import music.{Duration, Position}
import protocol.{Command, Dispatcher}

private[server] class TrackCommandHandler @Inject() (
  dispatcher: Dispatcher[Command],
  state: TrackState
) {

  import TrackSymbols._

  dispatcher.subscribe[SetTimeSignature]{ command =>
    state.setTrackSymbol(Position(Duration.QUARTER, 0), command.t) // TODO: move explicit position away from here
    true
  }

  dispatcher.subscribe[SetKey] { command =>
    state.setTrackSymbol(Position(Duration.QUARTER, 0), command.k) // TODO: move explicit position away from here
    true
  }

  dispatcher.subscribe[AddNote] { command =>
    state.addTrackSymbol(
      command.position,
      command.note
    )
    true
  }

}

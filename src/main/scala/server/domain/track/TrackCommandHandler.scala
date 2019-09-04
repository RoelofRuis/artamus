package server.domain.track

import javax.inject.Inject
import protocol.{Command, Dispatcher}

private[server] class TrackCommandHandler @Inject() (
  dispatcher: Dispatcher[Command],
  state: TrackState
) {

  import TrackProperties._
  import TrackSymbols._

  dispatcher.subscribe[SetTimeSignature]{ command =>
    state.addTrackProperty(command.t)
    true
  }

  dispatcher.subscribe[SetKey] { command =>
    state.addTrackProperty(command.k)
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

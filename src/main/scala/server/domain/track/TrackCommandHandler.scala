package server.domain.track

import javax.inject.Inject
import protocol.Command
import protocol.ServerInterface.Dispatcher

private[server] class TrackCommandHandler @Inject() (
  dispatcher: Dispatcher[Command],
  state: TrackState
) {

  import server.model.TrackProperties._
  import server.model.TrackSymbols._

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
      (command.duration, command.midiPitch)
    )
    true
  }

}

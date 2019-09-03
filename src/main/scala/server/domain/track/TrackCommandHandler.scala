package server.domain.track

import javax.inject.Inject
import server.dispatchers.CommandDispatcherImpl
import server.dispatchers.CommandDispatcherImpl.CommandHandler

private[server] class TrackCommandHandler @Inject() (
  dispatcher: CommandDispatcherImpl,
  state: TrackState
) {

  import server.model.TrackProperties._
  import server.model.TrackSymbols._

  dispatcher.subscribe(CommandHandler[SetTimeSignature]{ command =>
    state.addTrackProperty(command.t)
    true
  })

  dispatcher.subscribe(CommandHandler[SetKey] { command =>
    state.addTrackProperty(command.k)
    true
  })

  dispatcher.subscribe(CommandHandler[AddNote] { command =>
    state.addTrackSymbol(
      command.position,
      (command.duration, command.midiPitch)
    )
    true
  })

}

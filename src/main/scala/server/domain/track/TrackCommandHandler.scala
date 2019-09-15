package server.domain.track

import javax.inject.Inject
import music.symbolic.Position
import music.symbolic.const.Durations
import protocol.Command
import pubsub.Dispatcher

private[server] class TrackCommandHandler @Inject() (
  dispatcher: Dispatcher[Command],
  state: TrackState
) {

  import music.symbolic.properties.Symbols._

  dispatcher.subscribe[NewTrack.type]{ _ =>
    state.reset()
    true
  }

  dispatcher.subscribe[SetTimeSignature]{ command =>
    // TODO: move explicit position away from here
    state.setTrackSymbol(Position(Durations.QUARTER, 0), command.t)
    true
  }

  dispatcher.subscribe[SetKey] { command =>
    // TODO: move explicit position away from here
    state.setTrackSymbol(Position(Durations.QUARTER, 0), command.k)
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

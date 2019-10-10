package server.domain.track

import javax.inject.Inject
import music.primitives.{Duration, Position}
import music.symbols.{MetaSymbol, Note}
import protocol.Command
import pubsub.Dispatcher

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
    state.addSymbol[MetaSymbol.type](Position(Duration.QUARTER, 0), MetaSymbol.timeSignature(command.t))
    true
  }

  dispatcher.subscribe[SetKey] { command =>
    // TODO: move explicit position away from here
    state.addSymbol[MetaSymbol.type](Position(Duration.QUARTER, 0), MetaSymbol.key(command.k))
    true
  }

  dispatcher.subscribe[AddNote] { command =>
    state.addSymbol[Note.type](
      command.position,
      Note(
        command.octave,
        command.pitchClass,
        command.duration
      )
    )
    true
  }

}

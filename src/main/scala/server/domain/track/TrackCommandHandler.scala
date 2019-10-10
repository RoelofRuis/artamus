package server.domain.track

import javax.inject.Inject
import music.primitives.{Duration, Position}
import music.collection.SymbolProperties
import music.Symbols.{MetaSymbol, Note}
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
    state.addSymbol[MetaSymbol.type](Position(Duration.QUARTER, 0), SymbolProperties.empty.add(command.t))
    true
  }

  dispatcher.subscribe[SetKey] { command =>
    // TODO: move explicit position away from here
    state.addSymbol[MetaSymbol.type](Position(Duration.QUARTER, 0), SymbolProperties.empty.add(command.k))
    true
  }

  dispatcher.subscribe[AddNote] { command =>
    state.addSymbol[Note.type](
      command.position,
      SymbolProperties
        .empty
        .add(command.octave)
        .add(command.pitchClass)
        .add(command.duration)
    )
    true
  }

}

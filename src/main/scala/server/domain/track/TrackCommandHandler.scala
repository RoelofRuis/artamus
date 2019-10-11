package server.domain.track

import javax.inject.Inject
import music.symbols.SymbolType
import protocol.Command
import pubsub.Dispatcher

import scala.language.existentials

private[server] class TrackCommandHandler @Inject() (
  dispatcher: Dispatcher[Command],
  state: TrackState
) {

  dispatcher.subscribe[NewTrack.type]{ _ =>
    state.reset()
    true
  }

  dispatcher.subscribe[CreateSymbol[S forSome { type S <: SymbolType }]]{ command =>
    state.createSymbol(command.position, command.symbol)
    true
  }

}

package server.domain.track

import javax.inject.Inject
import music.math.temporal.Window
import protocol.Command
import pubsub.Dispatcher

import scala.language.existentials

private[server] class TrackCommandHandler @Inject() (
  dispatcher: Dispatcher[Command],
  state: TrackState
) {

  dispatcher.subscribe[NewTrack.type]{ _ =>
    state.clear()
    true
  }

  dispatcher.subscribe[CreateNoteSymbol]{ command =>
    state.createSymbol(command.window, command.symbol)
    true
  }

  dispatcher.subscribe[CreateTimeSignatureSymbol]{ command =>
    state.createTimeSignature(command.position, command.symbol)
    true
  }

  dispatcher.subscribe[CreateKeySymbol]{ command =>
    state.createSymbol(Window.instantAt(command.position), command.symbol)
    true
  }

}

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
    state.edit(_.create(command.window, command.symbol))
    true
  }

  dispatcher.subscribe[CreateTimeSignatureSymbol]{ command =>
    state.edit(_.writeTimeSignature(command.position, command.ts))
    true
  }

  dispatcher.subscribe[CreateKeySymbol]{ command =>
    state.edit(_.create(Window.instantAt(command.position), command.symbol))
    true
  }

}

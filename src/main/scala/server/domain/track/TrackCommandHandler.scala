package server.domain.track

import javax.inject.Inject
import music.domain.track.TrackRepository
import music.math.temporal.Window
import protocol.Command
import pubsub.Dispatcher

import scala.language.existentials

private[server] class TrackCommandHandler @Inject() (
  dispatcher: Dispatcher[Command],
  savepoint: Savepoint
) {

  dispatcher.subscribe[NewTrack.type]{ _ =>
    savepoint.clear()
    true
  }

  dispatcher.subscribe[CreateNoteSymbol]{ command =>
    val edited = savepoint
      .getCurrentTrack
      .create(command.window, command.symbol)

    savepoint.writeEdit(edited)
    true
  }

  dispatcher.subscribe[CreateTimeSignatureSymbol]{ command =>
    val edited = savepoint
      .getCurrentTrack
      .writeTimeSignature(command.position, command.ts)

    savepoint.writeEdit(edited)
    true
  }

  dispatcher.subscribe[CreateKeySymbol]{ command =>
    val edited = savepoint
      .getCurrentTrack
      .create(Window.instantAt(command.position), command.symbol)

    savepoint.writeEdit(edited)
    true
  }

}

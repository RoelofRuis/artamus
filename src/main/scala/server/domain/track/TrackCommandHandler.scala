package server.domain.track

import javax.inject.Inject
import music.math.temporal.Window
import protocol.Command
import pubsub.Dispatcher
import server.Request

import scala.language.existentials

private[server] class TrackCommandHandler @Inject() (
  dispatcher: Dispatcher[Request, Command],
  savepoint: Savepoint
) {

  dispatcher.subscribe[NewTrack.type]{ _ =>
    savepoint.clear()
    true
  }

  dispatcher.subscribe[CreateNoteSymbol]{ req =>
    val edited = savepoint
      .getCurrentTrack
      .create(req.attributes.window, req.attributes.symbol)

    savepoint.writeEdit(edited)
    true
  }

  dispatcher.subscribe[CreateTimeSignatureSymbol]{ req =>
    val edited = savepoint
      .getCurrentTrack
      .writeTimeSignature(req.attributes.position, req.attributes.ts)

    savepoint.writeEdit(edited)
    true
  }

  dispatcher.subscribe[CreateKeySymbol]{ req =>
    val edited = savepoint
      .getCurrentTrack
      .create(Window.instantAt(req.attributes.position), req.attributes.symbol)

    savepoint.writeEdit(edited)
    true
  }

}

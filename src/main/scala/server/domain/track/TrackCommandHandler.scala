package server.domain.track

import javax.inject.Inject
import music.domain.track.TrackRepository
import music.math.temporal.Window
import protocol.Command
import pubsub.Dispatcher
import server.Request

import scala.language.existentials

private[server] class TrackCommandHandler @Inject() (
  repository: TrackRepository,
  dispatcher: Dispatcher[Request, Command],
  savepoint: Savepoint
) {
  // TODO: refactor duplicate parts

  dispatcher.subscribe[NewTrack.type]{ _ =>
    // workspace clear
    savepoint.clear()
    true
  }

  dispatcher.subscribe[CreateNoteSymbol]{ req =>
    val edited = req
      .user
      .workspace
      .editedTrack
      .map(_.create(req.attributes.window, req.attributes.symbol))

    edited match {
      case None =>
      case Some(track) => repository.write(track)
    }
    true
  }

  dispatcher.subscribe[CreateTimeSignatureSymbol]{ req =>
    val edited = req
      .user
      .workspace
      .editedTrack
      .map(_.writeTimeSignature(req.attributes.position, req.attributes.ts))

    edited match {
      case None =>
      case Some(track) => repository.write(track)
    }
    true
  }

  dispatcher.subscribe[CreateKeySymbol]{ req =>
    val edited = req
      .user
      .workspace
      .editedTrack
      .map(_.create(Window.instantAt(req.attributes.position), req.attributes.symbol))

    edited match {
      case None =>
      case Some(track) => repository.write(track)
    }
    true
  }

}

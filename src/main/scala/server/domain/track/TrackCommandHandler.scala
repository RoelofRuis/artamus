package server.domain.track

import javax.inject.Inject
import music.domain.track.TrackRepository
import music.domain.workspace.WorkspaceRepository
import music.math.temporal.Window
import protocol.Command
import pubsub.Dispatcher
import server.Request

import scala.language.existentials

private[server] class TrackCommandHandler @Inject() (
  trackRepo: TrackRepository,
  workspaceRepo: WorkspaceRepository,
  dispatcher: Dispatcher[Request, Command],
) {
  // TODO: refactor duplicate parts

  dispatcher.subscribe[NewTrack.type]{ req =>
    val edited = req
      .user
      .workspace
      .startNewEdit

    workspaceRepo.write(edited)
    true
  }

  dispatcher.subscribe[CreateNoteSymbol]{ req =>
    val edited = req
      .user
      .workspace
      .getEditedTrack
      .create(req.attributes.window, req.attributes.symbol)

    trackRepo.write(edited)
    true
  }

  dispatcher.subscribe[CreateTimeSignatureSymbol]{ req =>
    val edited = req
      .user
      .workspace
      .getEditedTrack
      .writeTimeSignature(req.attributes.position, req.attributes.ts)

    trackRepo.write(edited)
    true
  }

  dispatcher.subscribe[CreateKeySymbol]{ req =>
    val edited = req
      .user
      .workspace
      .getEditedTrack
      .create(Window.instantAt(req.attributes.position), req.attributes.symbol)

    trackRepo.write(edited)
    true
  }

}

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
  workspaceRepo: WorkspaceRepository,
  dispatcher: Dispatcher[Request, Command],
) {
  // TODO: refactor duplicate parts

  dispatcher.subscribe[NewTrack.type]{ req =>
    val edited = workspaceRepo
      .getByOwner(req.user)
      .startNewEdit

    workspaceRepo.write(edited)
    true
  }

  dispatcher.subscribe[CreateNoteSymbol]{ req =>
    val workspace = workspaceRepo.getByOwner(req.user)

    val edited = workspace
      .editedTrack
      .create(req.attributes.window, req.attributes.symbol)

    workspaceRepo.write(workspace.makeEdit(edited))
    true
  }

  dispatcher.subscribe[CreateTimeSignatureSymbol]{ req =>
    val workspace = workspaceRepo.getByOwner(req.user)

    val edited = workspace
      .editedTrack
      .writeTimeSignature(req.attributes.position, req.attributes.ts)

    workspaceRepo.write(workspace.makeEdit(edited))
    true
  }

  dispatcher.subscribe[CreateKeySymbol]{ req =>
    val workspace = workspaceRepo.getByOwner(req.user)

    val edited = workspace
      .editedTrack
      .create(Window.instantAt(req.attributes.position), req.attributes.symbol)

    workspaceRepo.write(workspace.makeEdit(edited))
    true
  }

}

package server.domain.track

import javax.inject.Inject
import music.domain.workspace.WorkspaceRepository
import protocol.Command
import pubsub.Dispatcher
import server.Request

import scala.language.existentials
import scala.util.Success

private[server] class TrackCommandHandler @Inject() (
  workspaceRepo: WorkspaceRepository,
  dispatcher: Dispatcher[Request, Command],
) {
  // TODO: refactor duplicate parts

  dispatcher.subscribe[NewTrack.type]{ req =>
    val edited = workspaceRepo
      .getByOwner(req.user)
      .startNewEdit

    workspaceRepo.put(edited)
    Success(true)
  }

  dispatcher.subscribe[WriteNote]{ req =>
    val workspace = workspaceRepo.getByOwner(req.user)

    val edited = workspace
      .editedTrack
      .create(req.attributes.window, req.attributes.symbol)

    workspaceRepo.put(workspace.makeEdit(edited))
    Success(true)
  }

  dispatcher.subscribe[WriteTimeSignature]{ req =>
    val workspace = workspaceRepo.getByOwner(req.user)

    val edited = workspace
      .editedTrack
      .writeTimeSignature(req.attributes.position, req.attributes.ts)

    workspaceRepo.put(workspace.makeEdit(edited))
    Success(true)
  }

  dispatcher.subscribe[WriteKey]{ req =>
    val workspace = workspaceRepo.getByOwner(req.user)

    val edited = workspace
      .editedTrack
      .writeKey(req.attributes.position, req.attributes.symbol)

    workspaceRepo.put(workspace.makeEdit(edited))
    Success(true)
  }

}

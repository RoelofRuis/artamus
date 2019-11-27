package server.domain.track

import javax.inject.Inject
import music.domain.workspace.WorkspaceRepository
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
    for {
      workspace <- workspaceRepo.getByOwner(req.user)
      newWorkspace = workspace.startNewEdit
      _ <- workspaceRepo.put(newWorkspace)
    } yield true
  }

  dispatcher.subscribe[WriteNote]{ req =>
    for {
      workspace <- workspaceRepo.getByOwner(req.user)
      editedTrack = workspace.editedTrack.create(req.attributes.window, req.attributes.symbol)
      _ <- workspaceRepo.put(workspace.makeEdit(editedTrack))
    }  yield true
  }

  dispatcher.subscribe[WriteTimeSignature]{ req =>
    for {
      workspace <- workspaceRepo.getByOwner(req.user)
      editedTrack = workspace.editedTrack.writeTimeSignature(req.attributes.position, req.attributes.ts)
      _ <- workspaceRepo.put(workspace.makeEdit(editedTrack))
    } yield true
  }

  dispatcher.subscribe[WriteKey]{ req =>
    for {
      workspace <- workspaceRepo.getByOwner(req.user)
      editedTrack = workspace.editedTrack.writeKey(req.attributes.position, req.attributes.symbol)
      _ <- workspaceRepo.put(workspace.makeEdit(editedTrack))
    } yield true
  }

}

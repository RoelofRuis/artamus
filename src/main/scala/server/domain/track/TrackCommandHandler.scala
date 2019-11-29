package server.domain.track

import javax.inject.Inject
import music.domain.track.TrackRepository
import music.domain.workspace.WorkspaceRepository
import protocol.Command
import pubsub.Dispatcher
import server.Request

import scala.language.existentials

private[server] class TrackCommandHandler @Inject() (
  workspaceRepo: WorkspaceRepository,
  trackRepo: TrackRepository,
  dispatcher: Dispatcher[Request, Command],
) {
  // TODO: refactor duplicate parts

  dispatcher.subscribe[NewTrack.type]{ req =>
    for {
      track <- trackRepo.create
      workspace <- workspaceRepo.getByOwner(req.user)
      newWorkspace = workspace.setTrackToEdit(track)
      _ <- workspaceRepo.put(newWorkspace)
    } yield true
  }

  dispatcher.subscribe[WriteNote]{ req =>
    for {
      workspace <- workspaceRepo.getByOwner(req.user)
      track <- trackRepo.getById(workspace.editedTrack)
      editedTrack = track.create(req.attributes.window, req.attributes.symbol)
      _ <- trackRepo.put(editedTrack)
    }  yield true
  }

  dispatcher.subscribe[WriteTimeSignature]{ req =>
    for {
      workspace <- workspaceRepo.getByOwner(req.user)
      track <- trackRepo.getById(workspace.editedTrack)
      editedTrack = track.writeTimeSignature(req.attributes.position, req.attributes.ts)
      _ <- trackRepo.put(editedTrack)
    } yield true
  }

  dispatcher.subscribe[WriteKey]{ req =>
    for {
      workspace <- workspaceRepo.getByOwner(req.user)
      track <- trackRepo.getById(workspace.editedTrack)
      editedTrack = track.writeKey(req.attributes.position, req.attributes.symbol)
      _ <- trackRepo.put(editedTrack)
    } yield true
  }

}

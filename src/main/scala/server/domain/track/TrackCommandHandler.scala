package server.domain.track

import javax.inject.Inject
import music.domain.track.{Track, TrackRepository}
import music.domain.workspace.{Workspace, WorkspaceRepository}
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

  dispatcher.subscribe[NewWorkspace.type]{ req =>
    for {
      nextId <- trackRepo.nextId
      track = Track(nextId)
      workspace = Workspace(req.user.id, track.id)
      newWorkspace = workspace.setTrackToEdit(track)
      _ <- trackRepo.put(track)
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

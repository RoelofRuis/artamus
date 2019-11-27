package server.domain.track

import javax.inject.Inject
import music.domain.track.{Track, TrackRepository}
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
      workspace <- workspaceRepo.getByOwner(req.user)
      track <- trackRepo.put(Track())
      newWorkspace = workspace.setTrackToEdit(track)
      _ <- workspaceRepo.put(newWorkspace)
    } yield true
  }

  dispatcher.subscribe[WriteNote]{ req =>
    for {
      workspace <- workspaceRepo.getByOwner(req.user)
      track <- workspace.editedTrack.flatMap(trackRepo.getById).getOrElse(trackRepo.put(Track()))
      editedWorkspace = workspace.setTrackToEdit(track)
      editedTrack = track.create(req.attributes.window, req.attributes.symbol)
      _ <- trackRepo.put(editedTrack)
      _ <- workspaceRepo.put(editedWorkspace)
    }  yield true
  }

  dispatcher.subscribe[WriteTimeSignature]{ req =>
    for {
      workspace <- workspaceRepo.getByOwner(req.user)
      track <- workspace.editedTrack.flatMap(trackRepo.getById).getOrElse(trackRepo.put(Track()))
      editedWorkspace = workspace.setTrackToEdit(track)
      editedTrack = track.writeTimeSignature(req.attributes.position, req.attributes.ts)
      _ <- trackRepo.put(editedTrack)
      _ <- workspaceRepo.put(editedWorkspace)
    } yield true
  }

  dispatcher.subscribe[WriteKey]{ req =>
    for {
      workspace <- workspaceRepo.getByOwner(req.user)
      track <- workspace.editedTrack.flatMap(trackRepo.getById).getOrElse(trackRepo.put(Track()))
      editedWorkspace = workspace.setTrackToEdit(track)
      editedTrack = track.writeKey(req.attributes.position, req.attributes.symbol)
      _ <- workspaceRepo.put(editedWorkspace)
      _ <- trackRepo.put(editedTrack)
    } yield true
  }

}

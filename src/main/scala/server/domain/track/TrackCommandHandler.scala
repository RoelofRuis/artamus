package server.domain.track

import javax.inject.Inject
import music.domain.track.{Track, TrackRepository}
import music.domain.user.User
import music.domain.workspace.{Workspace, WorkspaceRepository}
import protocol.Command
import pubsub.Dispatcher
import server.Request

import scala.language.existentials
import scala.util.Try

private[server] class TrackCommandHandler @Inject() (
  workspaceRepo: WorkspaceRepository,
  trackRepo: TrackRepository,
  dispatcher: Dispatcher[Request, Command],
) {

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

  dispatcher.subscribe[WriteNoteGroup]{ req =>
    updateTrack(req.user, _.writeNoteGroup(req.attributes.group))
  }

  dispatcher.subscribe[WriteTimeSignature]{ req =>
    updateTrack(req.user, _.writeTimeSignature(req.attributes.position, req.attributes.ts))
  }

  dispatcher.subscribe[WriteKey]{ req =>
    updateTrack(req.user, _.writeKey(req.attributes.position, req.attributes.symbol))
  }

  def updateTrack(user: User, f: Track => Track): Try[Boolean] = {
    for {
      workspace <- workspaceRepo.getByOwner(user)
      track <- trackRepo.getById(workspace.editedTrack)
      _ <- trackRepo.put(f(track))
    } yield true
  }

}

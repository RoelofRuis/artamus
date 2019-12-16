package server.domain.track

import javax.inject.Inject
import music.domain.track.Track
import music.domain.workspace.Workspace
import protocol.Command
import pubsub.Dispatcher
import server.Request
import server.entity.{EntityResult, NotFound}

import scala.language.existentials
import scala.util.{Failure, Success, Try}

private[server] class TrackCommandHandler @Inject() (
  dispatcher: Dispatcher[Request, Command],
) {

  import server.storage.Tracks._
  import server.storage.Workspaces._

  dispatcher.subscribe[NewWorkspace.type]{ req =>
    val delete = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      _ <- req.db.removeTrackById(workspace.editedTrack)
    } yield true

    val recoveredDelete = delete match {
      case Left(_: NotFound) => EntityResult.ok
      case l @ Left(_) => l
      case r @ Right(_) => r
    }

    val res = for {
      _ <- recoveredDelete
      track = Track()
      workspace = Workspace(req.user.id, track.id)
      newWorkspace = workspace.setTrackToEdit(track)
      _ <- req.db.saveTrack(track)
      _ <- req.db.saveWorkspace(newWorkspace)
    } yield true

    res.toTry
  }

  dispatcher.subscribe[WriteNoteGroup]{ req =>
    updateTrack(req, _.writeNoteGroup(req.attributes.group))
  }

  dispatcher.subscribe[WriteTimeSignature]{ req =>
    updateTrack(req, _.writeTimeSignature(req.attributes.position, req.attributes.ts))
  }

  dispatcher.subscribe[WriteKey]{ req =>
    updateTrack(req, _.writeKey(req.attributes.position, req.attributes.symbol))
  }

  def updateTrack(req: Request[Command], f: Track => Track): Try[Boolean] = {
    val res = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      track <- req.db.getTrackById(workspace.editedTrack)
      _ <- req.db.saveTrack(f(track))
    } yield true

    res match {
      case Left(ex) => Failure(ex)
      case Right(_) => Success(true)
    }
  }

}

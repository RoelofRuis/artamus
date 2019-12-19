package server.domain.writing

import javax.inject.Inject
import music.model.write.track.Track
import music.model.write.workspace.Workspace
import protocol.Command
import pubsub.Dispatcher
import server.{Request, Responses}
import storage.api.{ModelResult, NotFound}

import scala.language.existentials
import scala.util.Try

private[server] class TrackCommandHandler @Inject() (
  dispatcher: Dispatcher[Request, Command],
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  dispatcher.subscribe[NewWorkspace.type]{ req =>
    val delete = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      _ <- req.db.removeTrackById(workspace.editedTrack)
    } yield ()

    val recoveredDelete = delete match {
      case Left(_: NotFound) => ModelResult.ok
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
    } yield ()

    Responses.executed(res)
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

  def updateTrack(req: Request[Command], f: Track => Track): Try[Unit] = {
    val res = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      track <- req.db.getTrackById(workspace.editedTrack)
      _ <- req.db.saveTrack(f(track))
    } yield ()

    Responses.executed(res)
  }

}

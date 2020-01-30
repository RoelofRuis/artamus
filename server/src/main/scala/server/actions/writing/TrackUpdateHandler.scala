package server.actions.writing

import javax.inject.{Inject, Singleton}
import music.model.workspace.Workspace
import music.model.write.Track
import protocol.Command
import pubsub.Dispatcher
import server.Request
import server.actions.Responses
import server.analysis.blackboard.Controller
import storage.api.DbResult

import scala.util.Try

@Singleton
private[server] class TrackUpdateHandler @Inject() (
  dispatcher: Dispatcher[Request, Command],
  analysis: Controller[Track]
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  dispatcher.subscribe[NewWorkspace.type]{ req =>
    val delete = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      _ <- req.db.removeTrackById(workspace.selectedTrack)
    } yield ()

    val res = for {
      _ <- delete.ifNotFound(DbResult.ok)
      track = Track()
      workspace = Workspace(req.user.id, track.id)
      newWorkspace = workspace.selectTrack(track)
      _ <- req.db.saveTrack(track)
      _ <- req.db.saveWorkspace(newWorkspace)
    } yield ()

    Responses.executed(res)
  }

  dispatcher.subscribe[Analyse.type] { req =>
    updateTrack(req, analysis.run)
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
      track <- req.db.getTrackById(workspace.selectedTrack)
      _ <- req.db.saveTrack(f(track))
    } yield ()

    Responses.executed(res)
  }

}

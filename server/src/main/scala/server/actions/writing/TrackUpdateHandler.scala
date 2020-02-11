package server.actions.writing

import domain.interact.Command
import domain.interact.Write._
import domain.workspace.Workspace
import domain.write.Track
import javax.inject.{Inject, Singleton}
import server.ServerRequest
import server.actions.Responses
import server.analysis.blackboard.Controller
import server.infra.ServerDispatcher
import storage.api.DbResult

import scala.util.Try

@Singleton
private[server] class TrackUpdateHandler @Inject() (
  dispatcher: ServerDispatcher,
  analysis: Controller[Track]
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  dispatcher.subscribe[NewWorkspace.type]{ req =>
    val delete = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      _ <- req.db.removeTrackById(workspace.editingTrack)
    } yield ()

    val res = for {
      _ <- delete.ifNotFound(DbResult.ok)
      track = Track.emptyNotes
      workspace = Workspace(req.user.id, track.id)
      newWorkspace = workspace.editTrack(track)
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

  def updateTrack(req: ServerRequest[Command], f: Track => Track): Try[Unit] = {
    val res = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      track <- req.db.getTrackById(workspace.editingTrack)
      _ <- req.db.saveTrack(f(track))
    } yield ()

    Responses.executed(res)
  }

}

package server.actions.workspace

import domain.interact.Write.NewWorkspace
import domain.workspace.Workspace
import domain.write.Track
import javax.inject.{Inject, Singleton}
import server.actions.Responses
import server.infra.ServerDispatcher
import storage.api.DbResult

@Singleton
private[server] class WorkspaceCommandHandler @Inject() (
  dispatcher: ServerDispatcher,
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  dispatcher.subscribe[NewWorkspace.type] { req =>
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

}

package server.actions.workspace

import domain.interact.Write.NewWorkspace
import domain.workspace.Workspace
import domain.write.Track
import javax.inject.{Inject, Singleton}
import server.async.{ActionRegistration, ActionRequest}

@Singleton
private[server] class WorkspaceCommandHandler @Inject() (
  registry: ActionRegistration
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  registry.register[NewWorkspace.type] { task =>
    val oldDeleted = for {
      workspace <- task.db.getWorkspaceByOwner(task.user)
      _ <- task.db.removeTrackById(workspace.editingTrack)
    } yield ()

    val res = for {
      _ <- oldDeleted.okIfNotFound
      track = Track.empty
      workspace = Workspace(task.user.id, track.id)
      newWorkspace = workspace.editTrack(track)
      _ <- task.db.saveTrack(track)
      _ <- task.db.saveWorkspace(newWorkspace)
    } yield ()

    ActionRequest.handled(res)
  }

}

package server.actions.workspace

import nl.roelofruis.artamus.core.api.Write.NewWorkspace
import nl.roelofruis.artamus.core.model.workspace.Workspace
import nl.roelofruis.artamus.core.model.track.Track
import javax.inject.{Inject, Singleton}
import server.api.{CommandHandlerRegistration, CommandRequest}

@Singleton
private[server] class WorkspaceCommandHandler @Inject() (
  registry: CommandHandlerRegistration
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  registry.register[NewWorkspace.type] { req =>
    val oldDeleted = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      _ <- req.db.removeTrackById(workspace.editingTrack)
    } yield ()

    val res = for {
      _ <- oldDeleted.okIfNotFound
      track = Track.empty
      workspace = Workspace(req.user.id, track.id)
      newWorkspace = workspace.editTrack(track)
      _ <- req.db.saveTrack(track)
      _ <- req.db.saveWorkspace(newWorkspace)
    } yield ()

    CommandRequest.dbResult(res)
  }

}

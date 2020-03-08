package server.actions.write

import domain.interact.Display.Render
import javax.inject.{Inject, Singleton}
import server.async.{ActionRegistration, ActionRequest}
import server.rendering.AsyncRenderer

@Singleton
private[server] class TrackTaskHandler @Inject() (
  registry: ActionRegistration,
  renderer: AsyncRenderer,
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  registry.register[Render.type] { task =>
    for {
      workspace <- task.db.getWorkspaceByOwner(task.user)
      track <- task.db.getTrackById(workspace.editingTrack)
      _ = renderer.render(track)
    } yield ()

    ActionRequest.ok
  }

}

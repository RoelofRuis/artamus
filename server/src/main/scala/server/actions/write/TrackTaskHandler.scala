package server.actions.write

import domain.interact.Display.Render
import javax.inject.{Inject, Singleton}
import server.async.{CommandHandlerRegistration, CommandRequest}
import server.rendering.AsyncRenderer

@Singleton
private[server] class TrackTaskHandler @Inject() (
  registry: CommandHandlerRegistration,
  renderer: AsyncRenderer,
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  registry.register[Render.type] { req =>
    for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      track <- req.db.getTrackById(workspace.editingTrack)
      _ = renderer.render(track)
    } yield ()

    CommandRequest.ok
  }

}

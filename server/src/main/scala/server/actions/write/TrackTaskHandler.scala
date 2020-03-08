package server.actions.write

import domain.interact.Display.Render
import javax.inject.{Inject, Singleton}
import server.infra.{CommandHandlerRegistration, CommandRequest}
import server.rendering.Renderer

@Singleton
private[server] class TrackTaskHandler @Inject() (
  registry: CommandHandlerRegistration,
  renderer: Renderer,
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  registry.register[Render.type] { req =>
    val res = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      track <- req.db.getTrackById(workspace.editingTrack)
    } yield renderer.render(track, req.db)

    CommandRequest.handled(res)
  }

}

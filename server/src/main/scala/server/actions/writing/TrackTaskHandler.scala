package server.actions.writing

import domain.interact.Display.Render
import javax.inject.{Inject, Singleton}
import server.actions.Responses
import server.infra.ServerDispatcher
import server.rendering.AsyncRenderer

@Singleton
private[server] class TrackTaskHandler @Inject() (
  dispatcher: ServerDispatcher,
  renderer: AsyncRenderer,
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  dispatcher.subscribe[Render.type] { req =>
    for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      track <- req.db.getTrackById(workspace.editingTrack)
      _ = renderer.render(track)
    } yield ()

    Responses.ok
  }

}

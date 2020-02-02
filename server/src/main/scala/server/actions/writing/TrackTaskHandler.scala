package server.actions.writing

import api.Write.Render
import javax.inject.{Inject, Singleton}
import protocol.Command
import pubsub.Dispatcher
import server.rendering.AsyncRenderer
import server.Request
import server.actions.Responses

@Singleton
private[server] class TrackTaskHandler @Inject() (
  dispatcher: Dispatcher[Request, Command],
  renderer: AsyncRenderer,
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  dispatcher.subscribe[Render.type] { req =>
    for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      track <- req.db.getTrackById(workspace.selectedTrack)
      _ = renderer.render(track)
    } yield ()

    Responses.ok
  }

}

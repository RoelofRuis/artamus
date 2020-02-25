package server.actions.write

import domain.interact.Write.GetLayers
import javax.inject.{Inject, Singleton}
import server.actions.Responses
import server.infra.ServerDispatcher

@Singleton
private[server] class TrackQueryHandler @Inject() (
  dispatcher: ServerDispatcher
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  dispatcher.subscribe[GetLayers.type] { req =>
    val res = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      track <- req.db.getTrackById(workspace.editingTrack)
    } yield {
      track.layers.zipWithIndex.map { case ((layerId, layer), index) =>
        (index -> (layerId, layer.visible))
      }.toMap
    }

    Responses.returning(res)
  }

}

package server.actions.write

import artamus.core.api.Write.GetLayers
import javax.inject.{Inject, Singleton}
import server.api.{QueryDispatcher, QueryRequest}

@Singleton
private[server] class TrackQueryHandler @Inject() (
  dispatcher: QueryDispatcher
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

    QueryRequest.returning(res)
  }

}

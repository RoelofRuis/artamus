package server.storage.entity

import music.domain.render.Render
import music.domain.track.Track.TrackId
import server.storage.api.{DataKey, DbIO, DbRead}
import server.storage.model.DomainProtocol

object Renders {

  import server.storage.entity.EntityIO._

  private val KEY = DataKey("render")

  object RenderJsonProtocol extends DomainProtocol {
    final case class RenderMapModel(renders: Map[String, Render] = Map())

    implicit val renderModel = jsonFormat2(Render)
    implicit val renderMapModel = jsonFormat1(RenderMapModel)
  }

  import RenderJsonProtocol._

  implicit class RenderQueries(db: DbRead) {
    def getRenderByTrackId(trackId: TrackId): EntityResult[Render] = {
      db.readModel[RenderMapModel](KEY).flatMap {
        _.renders.get(trackId.id.toString) match {
          case None => EntityResult.notFound
          case Some(u) => EntityResult.found(u)
        }
      }
    }
  }

  implicit class RenderCommands(db: DbIO) {
    def saveRender(render: Render): EntityResult[Unit] = {
      db.updateModel[RenderMapModel](
        KEY,
        RenderMapModel(),
        model => RenderMapModel(model.renders.updated(render.trackId.id.toString, render))
      )
    }
  }

}

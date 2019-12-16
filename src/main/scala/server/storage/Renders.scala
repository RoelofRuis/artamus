package server.storage

import music.domain.write.render.Render
import music.domain.write.track.Track.TrackId
import server.entity.EntityResult
import server.storage.api.{DataKey, DbIO}
import storage.api.DbRead

object Renders {

  import storage.EntityIO._

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

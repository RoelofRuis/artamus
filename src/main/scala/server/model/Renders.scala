package server.model

import music.domain.write.render.Render
import music.domain.write.track.Track.TrackId
import storage.api.{DataKey, DbIO, DbRead, ModelResult}

object Renders {

  import storage.api.ModelIO._

  private val KEY = DataKey("render")

  object RenderJsonProtocol extends DomainProtocol {
    final case class RenderMapModel(renders: Map[String, Render] = Map())

    implicit val renderModel = jsonFormat2(Render)
    implicit val renderMapModel = jsonFormat1(RenderMapModel)
  }

  import RenderJsonProtocol._

  implicit class RenderQueries(db: DbRead) {
    def getRenderByTrackId(trackId: TrackId): ModelResult[Render] = {
      db.readModel[RenderMapModel](KEY).flatMap {
        _.renders.get(trackId.id.toString) match {
          case None => ModelResult.notFound
          case Some(u) => ModelResult.found(u)
        }
      }
    }
  }

  implicit class RenderCommands(db: DbIO) {
    def saveRender(render: Render): ModelResult[Unit] = {
      db.updateModel[RenderMapModel](
        KEY,
        RenderMapModel(),
        model => RenderMapModel(model.renders.updated(render.trackId.id.toString, render))
      )
    }
  }

}
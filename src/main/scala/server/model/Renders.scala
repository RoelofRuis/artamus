package server.model

import music.model.display.render.Render
import music.model.write.track.Track.TrackId
import storage.api.{DataKey, DbIO, DbRead}

object Renders {

  import storage.api.ModelIO._

  private val KEY = DataKey("render")

  object RenderJsonProtocol extends DomainProtocol {
    final case class RendersTable(renders: Map[String, Render] = Map())

    implicit val renderFormat = jsonFormat2(Render)
    implicit val renderTableFormat = jsonFormat1(RendersTable)
  }

  import RenderJsonProtocol._

  implicit class RenderQueries(db: DbRead) {
    def getRenderByTrackId(trackId: TrackId): ModelResult[Render] = {
      db.readModel[RendersTable](KEY).flatMap {
        _.renders.get(trackId.id.toString) match {
          case None => ModelResult.notFound
          case Some(u) => ModelResult.found(u)
        }
      }
    }
  }

  implicit class RenderCommands(db: DbIO) {
    def saveRender(render: Render): ModelResult[Unit] = {
      db.updateModel[RendersTable](
        KEY,
        RendersTable(),
        model => RendersTable(model.renders.updated(render.trackId.id.toString, render))
      )
    }
  }

}

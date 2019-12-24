package server.model

import music.model.display.render.Render
import music.model.write.track.Track.TrackId
import spray.json.RootJsonFormat
import storage.api.{DbIO, DbRead, ModelResult}

object Renders {

  private implicit val table: JsonTableModel[Render] = new JsonTableModel[Render] {
    override val tableName: String = "render"
    implicit val format: RootJsonFormat[Render] = jsonFormat2(Render)
  }

  implicit class RenderQueries(db: DbRead) {
    def getRenderByTrackId(trackId: TrackId): ModelResult[Render] = {
      storage.api.recoverNotFound(db.readModel[table.Shape], table.empty).flatMap {
        _.get(trackId.id.toString) match {
          case None => ModelResult.notFound
          case Some(u) => ModelResult.found(u)
        }
      }
    }
  }

  implicit class RenderCommands(db: DbIO) {
    def saveRender(render: Render): ModelResult[Unit] = {
      db.updateModel[table.Shape](
        table.empty,
        _.updated(render.trackId.id.toString, render)
      )
    }
  }

}

package server.model

import music.model.display.render.Render
import music.model.write.track.Track.TrackId
import spray.json.RootJsonFormat
import storage.api.{DbIO, DbResult, ModelReader}

object Renders {

  private implicit val table: JsonTableDataModel[Render] = new JsonTableDataModel[Render] {
    override val tableName: String = "render"
    implicit val format: RootJsonFormat[Render] = jsonFormat2(Render)
  }

  implicit class RenderQueries(db: ModelReader) {
    def getRenderByTrackId(trackId: TrackId): DbResult[Render] = {
      db.readModel[table.Shape].ifNotFound(table.empty).flatMap {
        _.get(trackId.id.toString) match {
          case None => DbResult.notFound
          case Some(u) => DbResult.found(u)
        }
      }
    }
  }

  implicit class RenderCommands(db: DbIO) {
    def saveRender(render: Render): DbResult[Unit] = {
      db.updateModel[table.Shape](
        table.empty,
        _.updated(render.trackId.id.toString, render)
      )
    }
  }

}

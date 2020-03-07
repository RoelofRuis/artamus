package server.model

import domain.display.render.Render
import domain.write.Track.TrackId
import storage.api.DataTypes.JSON
import storage.api.{DataTypes, DbResult, DbIO, DbReader}

object Renders {

  private implicit val table: JsonDataModel[Render, TrackId] = new JsonDataModel[Render, TrackId] {
    import spray.json._

    implicit val format: RootJsonFormat[Render] = jsonFormat2(Render)

    override val name: String = "render"
    override val dataType: DataTypes.DataType = JSON

    override def objectId(obj: Render): TrackId = obj.trackId
    override def serializeId(id: TrackId): String = id.id.toString
  }

  implicit class RenderQueries(db: DbReader) {
    def getRenderByTrackId(trackId: TrackId): DbResult[Render] = db.readRow(trackId)
  }

  implicit class RenderCommands(db: DbIO) {
    def saveRender(render: Render): DbResult[Unit] = {
      db.writeRow(render)
    }
  }

}

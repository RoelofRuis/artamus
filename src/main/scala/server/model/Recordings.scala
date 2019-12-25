package server.model

import music.model.record.Recording
import music.model.record.Recording.RecordingId
import spray.json.RootJsonFormat
import storage.api.{DbIO, ModelReader, DbResult}

object Recordings {

  private implicit val table: JsonTableDataModel[Recording] = new JsonTableDataModel[Recording] {
    override val tableName: String = "recording"
    implicit val format: RootJsonFormat[Recording] = jsonFormat4(Recording.apply)
  }

  implicit class RecordingQueries(db: ModelReader) {
    def getRecordingById(id: RecordingId): DbResult[Recording] = {
      db.readModel[table.Shape].ifNotFound(table.empty).flatMap {
        _.get(id.toString) match {
          case None => DbResult.notFound
          case Some(recording) => DbResult.found(recording)
        }
      }
    }
  }

  implicit class RecordingCommands(db: DbIO) {
    def saveRecording(recording: Recording): DbResult[Unit] = {
      db.updateModel[table.Shape](
        table.empty,
        _.updated(recording.id.toString, recording)
      )
    }
  }

}

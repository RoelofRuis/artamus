package server.model

import music.model.record.Recording
import music.model.record.Recording.RecordingId
import spray.json.RootJsonFormat
import storage.api.{DbIO, DbRead, DbResult}

object Recordings {

  private implicit val table: JsonTableModel[Recording] = new JsonTableModel[Recording] {
    override val tableName: String = "recording"
    implicit val format: RootJsonFormat[Recording] = jsonFormat4(Recording.apply)
  }

  implicit class RecordingQueries(db: DbRead) {
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

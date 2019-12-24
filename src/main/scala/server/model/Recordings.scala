package server.model

import music.model.record.Recording
import music.model.record.Recording.RecordingId
import spray.json.RootJsonFormat
import storage.api.{DbIO, DbRead, ModelResult}

object Recordings {

  private implicit val table: JsonTableModel[Recording] = new JsonTableModel[Recording] {
    override val tableName: String = "recording"
    implicit val format: RootJsonFormat[Recording] = jsonFormat4(Recording.apply)
  }

  implicit class RecordingQueries(db: DbRead) {
    def getRecordingById(id: RecordingId): ModelResult[Recording] = {
      storage.api.recoverNotFound(db.readModel[table.Shape], table.empty).flatMap {
        _.get(id.toString) match {
          case None => ModelResult.notFound
          case Some(recording) => ModelResult.found(recording)
        }
      }
    }
  }

  implicit class RecordingCommands(db: DbIO) {
    def saveRecording(recording: Recording): ModelResult[Unit] = {
      db.updateModel[table.Shape](
        table.empty,
        _.updated(recording.id.toString, recording)
      )
    }
  }

}

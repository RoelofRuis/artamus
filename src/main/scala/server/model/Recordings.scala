package server.model

import music.model.record.Recording
import music.model.record.Recording.RecordingId
import storage.api.{DataKey, DbIO, DbRead, ModelResult}

object Recordings {

  import storage.api.ModelIO._

  private val KEY = DataKey("recording")

  object RecordingJsonProtocol extends DomainProtocol {
    final case class RecordingTable(recordings: Map[String, Recording] = Map())

    implicit val recordingFormat = jsonFormat4(Recording.apply)
    implicit val recordingTableFormat = jsonFormat1(RecordingTable)
  }

  import RecordingJsonProtocol._

  implicit class RecordingQueries(db: DbRead) {
    def getRecordingById(id: RecordingId): ModelResult[Recording] = {
      db.readModel[RecordingTable](KEY).flatMap {
        _.recordings.get(id.toString) match {
          case None => ModelResult.notFound
          case Some(recording) => ModelResult.found(recording)
        }
      }
    }
  }

  implicit class RecordingCommands(db: DbIO) {
    def saveRecording(recording: Recording): ModelResult[Unit] = {
      db.updateModel[RecordingTable](
        KEY,
        RecordingTable(),
        model => RecordingTable(
          model.recordings.updated(recording.id.toString, recording)
        )
      )
    }
  }

}

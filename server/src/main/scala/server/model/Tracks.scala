package server.model

import music.math.temporal.Window
import music.model.write.Track.TrackId
import music.model.write._
import music.primitives._
import spray.json.RootJsonFormat
import storage.api.{DbIO, DbResult, ModelReader}

object Tracks {

  private final case class TrackContentModel(
    id: TrackId,
    bars: Map[String, TimeSignature],
    chords: Map[String, (Window, Chord)],
    keys: Map[String, Key],
    notes: Map[String, NoteGroup]
  )

  private implicit val table: JsonTableDataModel[TrackContentModel] = new JsonTableDataModel[TrackContentModel] {
    override val tableName: String = "track"
    override implicit val format: RootJsonFormat[TrackContentModel] = jsonFormat5(TrackContentModel)
  }

  implicit class TrackQueries(db: ModelReader) {
    def getTrackById(id: TrackId): DbResult[Track] = {
      db.readModel[table.Shape].ifNotFound(table.empty).flatMap {
        _.get(id.id.toString) match {
          case None => DbResult.notFound
          case Some(w) => DbResult.found(
            Track(
              w.id,
              TimeSignatures(table.loadPositions(w.bars)),
              Keys(table.loadPositions(w.keys)),
              Chords(table.loadPositions(w.chords)),
              Notes(table.loadPositions(w.notes))
            )
          )
        }
      }
    }
  }

  implicit class TrackCommands(db: DbIO) {
    def saveTrack(track: Track): DbResult[Unit] = {
      db.updateModel[table.Shape](
        table.empty,
        _.updated(
          track.id.id.toString,
          TrackContentModel(
            track.id,
            table.savePositions(track.timeSignatures.timeSignatures),
            table.savePositions(track.chords.chords),
            table.savePositions(track.keys.keys),
            table.savePositions(track.notes.notes)
          )
        )
      )
    }

    def removeTrackById(trackId: TrackId): DbResult[Unit] = {
      db.updateModel[table.Shape](
        table.empty,
        _.removed(trackId.id.toString)
      )
    }
  }
}

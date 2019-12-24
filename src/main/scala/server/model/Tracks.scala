package server.model

import music.math.temporal.Window
import music.model.write.track.Track.TrackId
import music.model.write.track._
import music.primitives._
import spray.json.RootJsonFormat
import storage.api.{DbIO, DbRead, ModelResult}

object Tracks {

  private final case class TrackContentModel(
    id: TrackId,
    bars: Map[String, TimeSignature],
    chords: Map[String, (Window, Chord)],
    keys: Map[String, Key],
    notes: Map[String, NoteGroup]
  )

  private implicit val table: JsonTableModel[TrackContentModel] = new JsonTableModel[TrackContentModel] {
    override val tableName: String = "track"
    override implicit val format: RootJsonFormat[TrackContentModel] = jsonFormat5(TrackContentModel)
  }

  implicit class TrackQueries(db: DbRead) {
    def getTrackById(id: TrackId): ModelResult[Track] = {
      storage.api.recoverNotFound(db.readModel[table.Shape], table.empty).flatMap {
        _.get(id.id.toString) match {
          case None => ModelResult.notFound
          case Some(w) => ModelResult.found(
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
    def saveTrack(track: Track): ModelResult[Unit] = {
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

    def removeTrackById(trackId: TrackId): ModelResult[Unit] = {
      db.updateModel[table.Shape](
        table.empty,
        _.removed(trackId.id.toString)
      )
    }
  }
}

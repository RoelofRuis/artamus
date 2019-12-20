package server.model

import music.primitives._
import music.model.write.track.Track.TrackId
import music.model.write.track._
import music.math.temporal.Window
import storage.api.{DataKey, DbIO, DbRead, ModelResult}

object Tracks {

  import storage.api.ModelIO._

  private val KEY = DataKey("track")

  object TrackJsonProtocol extends DomainProtocol {
    final case class TrackTable(tracks: Map[String, TrackContentModel] = Map())
    final case class TrackContentModel(
      id: TrackId,
      bars: Map[String, TimeSignature],
      chords: Map[String, (Window, Chord)],
      keys: Map[String, Key],
      notes: Map[String, NoteGroup]
    )

    implicit val trackFormat = jsonFormat5(TrackContentModel)
    implicit val trackTableFormat = jsonFormat1(TrackTable)
  }

  import TrackJsonProtocol._

  implicit class TrackQueries(db: DbRead) {
    def getTrackById(id: TrackId): ModelResult[Track] = {
      db.readModel[TrackTable](KEY).flatMap {
        _.tracks.get(id.id.toString) match {
          case None => ModelResult.notFound
          case Some(w) => ModelResult.found(
            Track(
              w.id,
              TimeSignatures(loadPositions(w.bars)),
              Keys(loadPositions(w.keys)),
              Chords(loadPositions(w.chords)),
              Notes(loadPositions(w.notes))
            )
          )
        }
      }
    }
  }

  implicit class TrackCommands(db: DbIO) {
    def saveTrack(track: Track): ModelResult[Unit] = {
      db.updateModel[TrackTable](
        KEY,
        TrackTable(),
        model => TrackTable(
          model.tracks.updated(
            track.id.id.toString,
            TrackContentModel(
              track.id,
              savePositions(track.timeSignatures.timeSignatures),
              savePositions(track.chords.chords),
              savePositions(track.keys.keys),
              savePositions(track.notes.notes)
            )
          )
        )
      )
    }

    def removeTrackById(trackId: TrackId): ModelResult[Unit] = {
      db.updateModel[TrackTable](
        KEY,
        TrackTable(),
        model => TrackTable(model.tracks.removed(trackId.id.toString))
      )
    }
  }
}

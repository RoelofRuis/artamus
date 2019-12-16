package server.storage

import music.domain.track.Track.TrackId
import music.domain.track._
import music.math.temporal.Window
import music.primitives._
import server.entity.EntityResult
import server.storage.api.{DataKey, DbIO}
import storage.api.DbRead

object Tracks {

  import storage.EntityIO._

  private val KEY = DataKey("track")

  object TrackJsonProtocol extends DomainProtocol {
    final case class TrackMapModel(tracks: Map[String, TrackContentModel] = Map())
    final case class TrackContentModel(
      id: TrackId,
      bars: Map[String, TimeSignature],
      chords: Map[String, (Window, Chord)],
      keys: Map[String, Key],
      notes: Map[String, NoteGroup]
    )

    implicit val trackFormat = jsonFormat5(TrackContentModel)
    implicit val trackContentModelFormat = jsonFormat1(TrackMapModel)
  }

  import TrackJsonProtocol._

  implicit class TrackQueries(db: DbRead) {
    def getTrackById(id: TrackId): EntityResult[Track] = {
      db.readModel[TrackMapModel](KEY).flatMap {
        _.tracks.get(id.id.toString) match {
          case None => EntityResult.notFound
          case Some(w) => EntityResult.found(
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
    def saveTrack(track: Track): EntityResult[Unit] = {
      db.updateModel[TrackMapModel](
        KEY,
        TrackMapModel(),
        model => TrackMapModel(
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

    def removeTrackById(trackId: TrackId): EntityResult[Unit] = {
      db.updateModel[TrackMapModel](
        KEY,
        TrackMapModel(),
        model => TrackMapModel(model.tracks.removed(trackId.id.toString))
      )
    }
  }
}

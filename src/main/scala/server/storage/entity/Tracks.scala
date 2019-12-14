package server.storage.entity

import music.domain.track.Track.TrackId
import music.domain.track._
import music.math.temporal.Window
import music.primitives._
import server.storage.api.{DataKey, DbIO, DbRead, FileNotFound}
import server.storage.model.DomainProtocol

object Tracks {

  import server.storage.JsonDB._

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
      db.read[TrackMapModel](KEY) match {
        case Left(_: FileNotFound) => EntityResult.notFound
        case Left(ex) => EntityResult.badData(ex)
        case Right(model) =>
          model.tracks.get(id.id.toString) match {
            case None => EntityResult.notFound
            case Some(w) => EntityResult.found(
              Track(
                w.id,
                Bars(loadPositions(w.bars)),
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
    // TODO: condense the shit out of this logic!
    def saveTrack(track: Track): EntityResult[Unit] = {
      def update(model: TrackMapModel): TrackMapModel = {
        TrackMapModel(
          model.tracks.updated(
            track.id.id.toString,
            TrackContentModel(
              track.id,
              savePositions(track.bars.timeSignatures),
              savePositions(track.chords.chords),
              savePositions(track.keys.keys),
              savePositions(track.notes.notes)
            )
          )
        )
      }
      for {
        model <- read
        _ <- write(update(model))
      } yield ()
    }

    def removeTrackById(trackId: TrackId): EntityResult[Unit] = {
      def update(model: TrackMapModel): TrackMapModel = {
        TrackMapModel(model.tracks.removed(trackId.id.toString))
      }
      for {
        model <- read
        _ <- write(update(model))
      } yield ()
    }

    private def read: EntityResult[TrackMapModel] = {
      db.read[TrackMapModel](KEY) match {
        case Left(_: FileNotFound) => EntityResult.found(TrackMapModel())
        case Right(model) => EntityResult.found(model)
        case Left(ex) => EntityResult.badData(ex)
      }
    }

    private def write(model: TrackMapModel): EntityResult[Unit] = {
      db.write(KEY, model) match {
        case Right(_) => EntityResult.ok
        case Left(ex) => EntityResult.badData(ex)
      }
    }
  }
}

package server.storage.file

import javax.inject.{Inject, Singleton}
import music.domain.track.Track.TrackId
import music.domain.track._
import music.math.temporal.Window
import music.primitives._
import server.storage.file.db.{JsonFileDB, Query}
import server.storage.file.model.DomainProtocol

import scala.util.{Failure, Success, Try}

@Singleton
class FileTrackRepository @Inject() (
  db: JsonFileDB,
) extends TrackRepository {

  private val ID = "track"

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

  def removeById(trackId: TrackId): Try[Unit] = {
    db.update[TrackMapModel](ID, TrackMapModel()) { storage =>
      TrackMapModel(storage.tracks.removed(trackId.id.toString))
    }
  }

  override def put(track: Track): Try[Unit] = {
    db.update[TrackMapModel](ID, TrackMapModel()) { storage =>
        TrackMapModel(
          storage.tracks.updated(
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
  }

  override def getById(id: TrackId): Try[Track] = {
    db.readByQuery[TrackMapModel, TrackContentModel](Query(ID, _.tracks.get(id.id.toString))) match {
      case Failure(ex) => Failure(ex)
      case Success(model) =>
        Success(Track(
          model.id,
          Bars(loadPositions(model.bars)),
          Keys(loadPositions(model.keys)),
          Chords(loadPositions(model.chords)),
          Notes(loadPositions(model.notes))
        )
      )
    }
  }

}

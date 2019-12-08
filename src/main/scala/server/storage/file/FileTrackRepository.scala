package server.storage.file

import javax.inject.{Inject, Singleton}
import music.domain.track.Track.TrackId
import music.domain.track._
import music.math.temporal.Window
import music.primitives._
import server.storage.EntityNotFoundException
import server.storage.file.db.JsonFileDB
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

  override def nextId: Try[TrackId] = Success(TrackId(0)) // TODO: proper implementation

  override def put(track: Track): Try[Unit] = {
    db.update(ID, TrackMapModel()) { storage =>
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
    db.read[TrackMapModel](ID, TrackMapModel()) match {
      case Failure(ex) => Failure(ex)
      case Success(storage) => storage.tracks.get(id.id.toString) match {
        case None => Failure(EntityNotFoundException("Track"))
        case Some(model) =>
          Success(Track(
            model.id,
            Bars(loadPositions(model.bars)),
            Keys(loadPositions(model.keys)),
            Chords(loadPositions(model.chords)),
            Notes(loadPositions(model.notes))
          ))
      }
    }
  }

}

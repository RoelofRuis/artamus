package application.repository

import application.api.KeyValueStorage
import application.model.Track
import javax.inject.Inject

import scala.util.{Failure, Success, Try}

/** @deprecated */
class TrackRepository @Inject() (storage: KeyValueStorage[Track.TrackID, Track]) {

  def add(track: Track): Track = {
    storage.put(track.id, track)

    track
  }

  def get(id: Track.TrackID): Try[Track] = {
    storage.get(id) match {
      case Some(idea) => Success(idea)
      case None => Failure(NotFoundException(s"No Track with ID [$id]"))
    }
  }

  def getAll: Vector[Track] = storage.getAll

}

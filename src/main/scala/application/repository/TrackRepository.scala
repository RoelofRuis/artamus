package application.repository

import application.api.Commands.TrackID
import application.api.KeyValueStorage
import application.model.Track
import javax.inject.Inject

import scala.util.{Failure, Success, Try}

/** @deprecated */
class TrackRepository @Inject() (storage: KeyValueStorage[TrackID, Track]) {

  def add(track: Track): (TrackID, Track) = {
    val nextId = TrackID(storage.nextId)

    storage.put(nextId, track)

    (nextId, track)
  }

  def get(id: TrackID): Try[Track] = {
    storage.get(id) match {
      case Some(idea) => Success(idea)
      case None => Failure(NotFoundException(s"No Track with ID [$id]"))
    }
  }

  def getAllIds: Vector[TrackID] = storage.getAllKeys

}

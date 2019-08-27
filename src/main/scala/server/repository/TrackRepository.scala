package server.repository

import server.api.Commands.TrackID
import server.api.KeyValueStorage
import server.model.Track
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

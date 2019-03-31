package application.repository

import application.api.KeyValueStorage
import application.model.event.Track
import application.model.event.Track.{TrackElements, Track_ID}
import application.model.event.domain.{ID, Ticks}
import javax.inject.Inject

import scala.util.{Failure, Success, Try}

class TrackRepository @Inject() (storage: KeyValueStorage[Track_ID, Track]) {

  def add(
    ticksPerQuarter: Ticks,
    elements: TrackElements
  ): Track = {
    val id = ID[Track](storage.nextId)
    val track = Track(id, ticksPerQuarter, elements)

    storage.put(id, track)

    track
  }

  def get(id: Track_ID): Try[Track] = {
    storage.get(id) match {
      case Some(idea) => Success(idea)
      case None => Failure(NotFoundException(s"No Track with ID [$id]"))
    }
  }

  def getAll: Vector[Track] = storage.getAll

}

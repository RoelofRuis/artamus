package application.repository

import application.api.KeyValueStorage
import application.model.event.MidiTrack
import application.model.event.MidiTrack.{TrackElements, Track_ID}
import application.model.event.domain.{ID, Ticks}
import javax.inject.Inject

import scala.util.{Failure, Success, Try}

/** @deprecated */
class TrackRepository @Inject() (storage: KeyValueStorage[Track_ID, MidiTrack]) {

  def add(
    ticksPerQuarter: Ticks,
    elements: TrackElements
  ): MidiTrack = {
    val id = ID[MidiTrack](storage.nextId)
    val track = MidiTrack(id, ticksPerQuarter, elements)

    storage.put(id, track)

    track
  }

  def get(id: Track_ID): Try[MidiTrack] = {
    storage.get(id) match {
      case Some(idea) => Success(idea)
      case None => Failure(NotFoundException(s"No Track with ID [$id]"))
    }
  }

  def getAll: Vector[MidiTrack] = storage.getAll

}

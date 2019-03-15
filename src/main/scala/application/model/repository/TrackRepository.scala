package application.model.repository

import application.model.Idea.Idea_ID
import application.model.Track.{TrackElements, TrackType, Track_ID}
import application.model._
import application.ports.KeyValueStorage
import javax.inject.Inject

class TrackRepository @Inject() (storage: KeyValueStorage[Track_ID, Track]) {

  def add(
    ideaId: Idea_ID,
    trackType: TrackType,
    ticksPerQuarter: Ticks,
    elements: TrackElements
  ): Track = {
    val id = ID[Track](storage.nextId)
    val track = Track(id, ideaId, trackType, ticksPerQuarter, elements)

    storage.put(id, track)

    track
  }

  def get(id: Track_ID): Option[Track] = storage.get(id)

  def getAll: Vector[Track] = storage.getAll

}

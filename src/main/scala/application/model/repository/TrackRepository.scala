package application.model.repository

import application.model.Track.TrackType
import application.model._
import application.ports.KeyValueStorage
import javax.inject.Inject

class TrackRepository @Inject() (
  storage: KeyValueStorage[(ID[Idea.type], TrackType), Track[Note]],
) {

  def store(idea: ID[Idea.type], trackType: TrackType, track: Track[Note]): Unit = storage.put((idea, trackType), track)

  def retrieve(idea: ID[Idea.type], trackType: TrackType): Option[Track[Note]] = storage.get((idea, trackType))

}

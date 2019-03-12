package application.model.repository

import application.model.{Idea, Note, Track, TrackType}
import application.ports.KeyValueStorage
import javax.inject.Inject

class TrackRepository @Inject() (
  storage: KeyValueStorage[(Idea.ID, TrackType), Track[Note]],
) {

  def store(idea: Idea.ID, trackType: TrackType, track: Track[Note]): Unit = storage.put((idea, trackType), track)

  def retrieve(idea: Idea.ID, trackType: TrackType): Option[Track[Note]] = storage.get((idea, trackType))

}

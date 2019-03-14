package application.model.repository

import application.model.Idea.Idea_ID
import application.model.Track.TrackType
import application.model._
import application.ports.KeyValueStorage
import javax.inject.Inject

class TrackRepository @Inject() (
  storage: KeyValueStorage[(Idea_ID, TrackType), Track[Note]],
) {

  def store(idea: Idea_ID, trackType: TrackType, track: Track[Note]): Unit = storage.put((idea, trackType), track)

  def retrieve(idea: Idea_ID, trackType: TrackType): Option[Track[Note]] = storage.get((idea, trackType))

}

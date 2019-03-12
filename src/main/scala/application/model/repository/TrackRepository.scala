package application.model.repository

import application.model.{Idea, Note, Track}
import application.ports.KeyValueStorage
import javax.inject.Inject

class TrackRepository @Inject() (
  storage: KeyValueStorage[Idea.ID, Track[Note]],
) {

  def store(idea: Idea.ID, track: Track[Note]): Unit = storage.put(idea, track)

  def retrieve(idea: Idea.ID): Option[Track[Note]] = storage.get(idea)

}

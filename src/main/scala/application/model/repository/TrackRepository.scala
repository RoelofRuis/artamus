package application.model.repository

import application.model.Idea
import application.model.Unquantized.UnquantizedTrack
import application.ports.KeyValueStorage
import javax.inject.Inject

class TrackRepository @Inject() (storage: KeyValueStorage[Idea.ID, UnquantizedTrack]) {

  def storeUnquantized(idea: Idea.ID, track: UnquantizedTrack): Unit = storage.put(idea, track)

  def retrieve(idea: Idea.ID): Option[UnquantizedTrack] = storage.get(idea)

}

package application.model.repository

import application.model.Idea
import application.model.Quantized.QuantizedTrack
import application.model.Unquantized.UnquantizedTrack
import application.ports.KeyValueStorage
import javax.inject.Inject

class TrackRepository @Inject() (
  unquantizedStorage: KeyValueStorage[Idea.ID, UnquantizedTrack],
  quantizedStorage: KeyValueStorage[Idea.ID, QuantizedTrack]
) {

  def storeUnquantized(idea: Idea.ID, track: UnquantizedTrack): Unit = unquantizedStorage.put(idea, track)

  def storeQuantized(idea: Idea.ID, track: QuantizedTrack): Unit = quantizedStorage.put(idea, track)

  def retrieveUnquantized(idea: Idea.ID): Option[UnquantizedTrack] = unquantizedStorage.get(idea)

  def retrieveQuantized(idea: Idea.ID): Option[QuantizedTrack] = quantizedStorage.get(idea)

}

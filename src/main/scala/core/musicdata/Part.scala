package core.musicdata

import com.google.inject.Inject
import core.components.KeyValueStorage
import core.idea.Idea

case class Part(grid: MusicGrid)

class PartRepository @Inject() (storage: KeyValueStorage[Idea.ID, Part]) {

  def store(idea: Idea.ID, part: Part): Unit = storage.put(idea, part)

  def retrieve(idea: Idea.ID): Option[Part] = storage.get(idea)

}
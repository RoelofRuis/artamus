package core.musicdata

import com.google.inject.Inject
import core.components.KeyValueStorage
import core.idea.Idea
import core.symbolic.Music.Grid

class GridRepository @Inject() (storage: KeyValueStorage[Idea.ID, Grid]) {

  def store(idea: Idea.ID, grid: Grid): Unit = storage.put(idea, grid)

  def retrieve(idea: Idea.ID): Option[Grid] = storage.get(idea)

}

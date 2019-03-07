package application.musicdata

import application.components.storage.KeyValueStorage
import com.google.inject.Inject
import application.idea.Idea
import application.symbolic.Music.Grid

class GridRepository @Inject() (storage: KeyValueStorage[Idea.ID, Grid]) {

  def store(idea: Idea.ID, grid: Grid): Unit = storage.put(idea, grid)

  def retrieve(idea: Idea.ID): Option[Grid] = storage.get(idea)

}

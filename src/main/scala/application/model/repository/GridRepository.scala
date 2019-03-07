package application.model.repository

import application.model.Idea
import application.ports.KeyValueStorage
import application.model.Music.Grid
import com.google.inject.Inject

class GridRepository @Inject() (storage: KeyValueStorage[Idea.ID, Grid]) {

  def store(idea: Idea.ID, grid: Grid): Unit = storage.put(idea, grid)

  def retrieve(idea: Idea.ID): Option[Grid] = storage.get(idea)

}

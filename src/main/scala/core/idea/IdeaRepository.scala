package core.idea

import com.google.inject.Inject
import core.components.Storage

class IdeaRepository @Inject() (ideaStorage: Storage[Idea]) {

  def add(title: String): Idea = {
    val idea = Idea(ideaStorage.getNextID, title)
    ideaStorage.put(idea)

    idea
  }

  def getAll: Vector[Idea] = ideaStorage.getAll

}

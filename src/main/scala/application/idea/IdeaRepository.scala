package application.idea

import application.ports.storage.Storage
import com.google.inject.Inject

class IdeaRepository @Inject() (ideaStorage: Storage[Idea]) {

  def add(title: String): Idea = {
    val idea = Idea(ideaStorage.getNextID, title)
    ideaStorage.put(idea)

    idea
  }

  def getAll: Vector[Idea] = ideaStorage.getAll

}

package application.model.repository

import application.model.Idea
import application.ports.Storage
import javax.inject.Inject

class IdeaRepository @Inject() (ideaStorage: Storage[Idea]) {

  def add(title: String): Idea = {
    val idea = Idea(ideaStorage.getNextID, title)
    ideaStorage.put(idea)

    idea
  }

  def getAll: Vector[Idea] = ideaStorage.getAll

}

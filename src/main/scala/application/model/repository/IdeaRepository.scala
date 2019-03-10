package application.model.repository

import application.model.Idea
import application.ports.KeyValueStorage
import javax.inject.Inject

class IdeaRepository @Inject() (ideaStorage: KeyValueStorage[Idea.ID, Idea]) {

  def add(title: String): Idea = {
    val id = Idea.ID(ideaStorage.nextId)
    val idea = Idea(id, title)

    ideaStorage.put(id, idea)

    idea
  }

  def getAll: Vector[Idea] = ideaStorage.getAll

}

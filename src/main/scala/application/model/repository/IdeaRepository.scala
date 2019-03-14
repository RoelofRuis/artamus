package application.model.repository

import application.model.{ID, Idea}
import application.ports.KeyValueStorage
import javax.inject.Inject

class IdeaRepository @Inject() (ideaStorage: KeyValueStorage[ID[Idea.type], Idea]) {

  def add(title: String): Idea = {
    val id = ID[Idea.type](ideaStorage.nextId)
    val idea = Idea(id, title)

    ideaStorage.put(id, idea)

    idea
  }

  def getAll: Vector[Idea] = ideaStorage.getAll

}

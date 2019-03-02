package core.idea

import com.google.inject.Inject
import core.ID
import core.components.Storage

case class Idea(id: ID, title: String)

class IdeaRepository @Inject() (storage: Storage[Idea]) {

  def add(title: String): Idea = {
    val idea = Idea(storage.getNextID, title)
    storage.put(idea)

    idea
  }

  def getAll: Vector[Idea] = storage.getAll

}


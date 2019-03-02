package core.idea

import com.google.inject.Inject
import core.components.Storage

case class Idea(id: Long, title: String)

class IdeaRepository @Inject() (storage: Storage[Idea]) {

  def add(title: String): Long = {
    val id = storage.getNextId
    storage.put(Idea(id, title))

    id
  }

  def getAll: Vector[Idea] = storage.getAll

}


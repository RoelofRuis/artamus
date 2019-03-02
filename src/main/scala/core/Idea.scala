package core

import com.google.inject.Inject
import core.components.Storage

case class Idea(id: Long, title: String)

class IdeaRepository @Inject() (storage: Storage[Idea]) {

  def add(title: String): Unit = storage.put(Idea(storage.getNextId, title))

  def getAll: Vector[Idea] = storage.getAll

}


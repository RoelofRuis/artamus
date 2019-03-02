package core

import com.google.inject.Inject
import core.components.Storage

case class Idea(title: String)

class IdeaRepository @Inject() (storage: Storage[Idea]) {

  def add(title: String): Unit = storage.put(Idea(title))

  def getAll: Vector[Idea] = storage.getAll

}


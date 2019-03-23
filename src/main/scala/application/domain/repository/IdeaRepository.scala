package application.domain.repository

import application.api.KeyValueStorage
import application.domain.Idea.Idea_ID
import application.domain.{ID, Idea}
import javax.inject.Inject

import scala.util.{Failure, Success, Try}

class IdeaRepository @Inject() (storage: KeyValueStorage[Idea_ID, Idea]) {

  def add(title: String): Idea = {
    val id = ID[Idea](storage.nextId)
    val idea = Idea(id, title)

    storage.put(id, idea)

    idea
  }

  def get(id: Idea_ID): Try[Idea] = {
    storage.get(id) match {
      case Some(idea) => Success(idea)
      case None => Failure(NotFoundException(s"No Idea with ID [$id]"))
    }
  }

  def getAll: Vector[Idea] = storage.getAll

}

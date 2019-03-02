package core

import com.google.inject.Inject
import core.components.Storage

import scala.collection.mutable.ListBuffer

case class Idea(title: String)

class IdeaRepository @Inject() (storage: Storage[Idea]) {

  private var ideas = ListBuffer[Idea]()

  def add(title: String): Unit = ideas += Idea(title)

  def getAll: Vector[Idea] = ideas.toVector

}


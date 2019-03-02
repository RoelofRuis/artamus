package core

import scala.collection.mutable.ListBuffer

case class Idea(title: String)

class IdeaRepository {

  private var ideas = ListBuffer[Idea]()

  def add(title: String): Unit = ideas += Idea(title)

  def getAll: Vector[Idea] = ideas.toVector

}


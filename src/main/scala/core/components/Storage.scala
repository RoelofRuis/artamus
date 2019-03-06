package core.components

import core.idea.Idea

trait Storage[A] {

  def getNextID: Idea.ID

  def put(thing: A): Unit

  def getAll: Vector[A]

}

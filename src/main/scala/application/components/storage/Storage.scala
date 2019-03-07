package application.components.storage

import application.idea.Idea

trait Storage[A] {

  def getNextID: Idea.ID

  def put(thing: A): Unit

  def getAll: Vector[A]

}

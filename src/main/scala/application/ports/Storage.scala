package application.ports

import application.model.Idea

/**
  * A simple storage interface to store some type A.
  */
trait Storage[A] {

  def getNextID: Idea.ID

  def put(thing: A): Unit

  def getAll: Vector[A]

}

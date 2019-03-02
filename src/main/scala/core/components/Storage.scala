package core.components

import core.ID

trait Storage[A] {

  def getNextID: ID

  def put(thing: A): Unit

  def getAll: Vector[A]

}

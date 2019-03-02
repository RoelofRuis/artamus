package core.components

trait Storage[A] {

  def getNextId: Long

  def put(thing: A): Unit

  def getAll: Vector[A]

}

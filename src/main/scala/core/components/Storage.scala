package core.components

trait Storage[A] {

  def put(thing: A): Unit

  def getAll: Vector[A]

}

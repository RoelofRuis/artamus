package storage

import core.components.Storage

import scala.collection.mutable.ListBuffer

class InMemoryStore[A] extends Storage[A] {

  private val buffer = ListBuffer[A]()

  override def put(thing: A): Unit = buffer :+ thing

  override def getAll: Vector[A] = buffer.toVector
}

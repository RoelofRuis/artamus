package storage.memory

import core.components.Storage

import scala.collection.mutable.ListBuffer

class InMemoryStorage[A] extends Storage[A] {

  private val buffer = ListBuffer[A]()

  override def put(thing: A): Unit = buffer :+ thing

  override def getAll: Vector[A] = buffer.toVector

  override def getNextId: Long = buffer.size + 1

}

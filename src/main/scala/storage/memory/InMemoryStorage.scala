package storage.memory

import application.model.Idea
import application.ports.Storage

import scala.collection.mutable.ListBuffer

class InMemoryStorage[A] extends Storage[A] {

  private val buffer = ListBuffer[A]()

  override def put(thing: A): Unit = buffer.append(thing)

  override def getAll: Vector[A] = buffer.toVector

  override def getNextID: Idea.ID = Idea.ID(buffer.size + 1)

}

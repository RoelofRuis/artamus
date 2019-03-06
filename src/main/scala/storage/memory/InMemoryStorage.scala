package storage.memory

import core.application.ServiceRegistry
import core.components.{Logger, Storage}
import core.idea.Idea
import javax.inject.Inject

import scala.collection.mutable.ListBuffer

class InMemoryStorage[A] @Inject() (logger: ServiceRegistry[Logger]) extends Storage[A] {

  private val buffer = ListBuffer[A]()

  override def put(thing: A): Unit = {
    logger.map(_.debug(s"[$this] inserting $thing"))
    buffer.append(thing)
  }

  override def getAll: Vector[A] = {
    logger.map(_.debug(s"[$this] getting all"))
    buffer.toVector
  }

  override def getNextID: Idea.ID = Idea.ID(buffer.size + 1)

}

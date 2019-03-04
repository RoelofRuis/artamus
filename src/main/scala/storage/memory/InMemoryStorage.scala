package storage.memory

import core.ID
import core.application.ServiceRegistry
import core.components.{Logger, Storage}
import javax.inject.Inject

import scala.collection.mutable.ListBuffer

class InMemoryStorage[A] @Inject() (logger: ServiceRegistry[Logger]) extends Storage[A] {

  private val buffer = ListBuffer[A]()

  override def put(thing: A): Unit = {
    logger.getActive.debug(s"[$this] inserting $thing")
    buffer.append(thing)
  }

  override def getAll: Vector[A] = {
    logger.getActive.debug(s"[$this] getting all")
    buffer.toVector
  }

  override def getNextID: ID = ID(buffer.size + 1)

}

package storage.memory

import core.components.{Logger, SequencesStorage}
import javax.inject.Inject

import scala.collection.mutable

class InMemorySequencesStorage[K, V] @Inject() (logger: Logger) extends SequencesStorage[K, V] {

  private val buffer = mutable.HashMap[K, Vector[V]]()

  def add(key: K, value: V): Unit = {
    buffer.put(key, buffer.getOrElse(key, Vector[V]()) :+ value)
  }

  def has(key: K): Boolean = buffer.isDefinedAt(key)

  def get(key: K): Vector[V] = buffer.getOrElse(key, Vector[V]())

}

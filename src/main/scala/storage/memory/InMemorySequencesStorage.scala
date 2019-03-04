package storage.memory

import core.components.SequencesStorage

import scala.collection.mutable

class InMemorySequencesStorage[K, V] extends SequencesStorage[K, V] {

  private val buffer = mutable.HashMap[K, Vector[V]]()

  def add(key: K, value: V): Unit = {
    buffer.put(key, buffer.getOrElse(key, Vector[V]()) :+ value)
  }

  def has(key: K): Boolean = buffer.isDefinedAt(key)

  def get(key: K): Vector[V] = buffer.getOrElse(key, Vector[V]())

}

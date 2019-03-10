package storage.memory

import application.ports.KeyValueStorage

import scala.collection.mutable

class InMemoryKeyValueStorage[K, V] extends KeyValueStorage[K, V] {

  private val buffer = mutable.HashMap[K, V]()

  def nextId: Long = buffer.size + 1

  def put(key: K, value: V): Unit = buffer.put(key, value)

  def get(key: K): Option[V] = buffer.get(key)

  def getAll: Vector[V] = buffer.values.toVector

}

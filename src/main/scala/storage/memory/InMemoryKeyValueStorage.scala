package storage.memory

import core.components.KeyValueStorage

import scala.collection.mutable

class InMemoryKeyValueStorage[K, V] extends KeyValueStorage[K, V] {

  private val buffer = mutable.HashMap[K, V]()

  def put(key: K, value: V): Unit = buffer.put(key, value)

  def get(key: K): Option[V] = buffer.get(key)

}

package storage.memory

import application.api.KeyValueStorage

import scala.collection.mutable

class InMemoryKeyValueStorage[K, V] extends KeyValueStorage[K, V] {

  private val buffer = mutable.HashMap[K, V]()

  def nextId: Long = buffer.size + 1

  def put(key: K, value: V): Unit = buffer.put(key, value)

  def get(key: K): Option[V] = buffer.get(key)

  def getAllKeys: Vector[K] = buffer.keys.toVector

  def getAll: Vector[V] = buffer.values.toVector

}

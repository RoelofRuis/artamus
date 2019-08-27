package server.api

/**
  * Simple key value storage.
  *
  * @tparam K The key type.
  * @tparam V The value type.
  */
trait KeyValueStorage[K, V] {

  def nextId: Long

  def put(key: K, value: V): Unit

  def get(key: K): Option[V]

  def getAllKeys: Vector[K]

  def getAll: Vector[V]

}

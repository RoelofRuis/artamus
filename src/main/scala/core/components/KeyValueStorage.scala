package core.components

trait KeyValueStorage[K, V] {

  def put(key: K, value: V): Unit

  def get(key: K): Option[V]

}

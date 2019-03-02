package core.components

trait SequencesStorage[K, V] {

  def add(key: K, value: V): Unit

  def get(key: K): Vector[V]

}

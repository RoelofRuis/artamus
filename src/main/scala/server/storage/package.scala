package server

package object storage {

  trait StorageConfig {
    val compactJson: Boolean
  }

  final case class EntityNotFoundException(name: String) extends Exception

}

package server

package object storage {

  final case class EntityNotFoundException(name: String) extends Exception

}
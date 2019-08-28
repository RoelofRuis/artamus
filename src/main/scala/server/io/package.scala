package server

package object io {

  case class InvalidRequestException(msg: String) extends RuntimeException {
    override def toString: String = msg
  }

  case class MissingHandlerException(msg: String) extends RuntimeException {
    override def toString: String = msg
  }

}

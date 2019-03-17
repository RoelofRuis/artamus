package application.model

package object repository {

  case class NotFoundException(msg: String) extends Exception {
    override def toString: String = msg
  }

}

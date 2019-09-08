package protocol2

import java.net.Socket

import scala.util.{Failure, Success}

class ServerObjectSocketFactory (socket: Socket) extends ObjectSocketFactory {

  def connect(): Either[String, ObjectSocketConnection] = {
    ObjectSocketConnection(socket) match {
      case Success(con) => Right(con)
      case Failure(err) => Left(s"Unable to open connection [$err]")
    }
  }

}


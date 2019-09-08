package protocol2

import java.net.InetAddress

import scala.util.{Failure, Success}

class ClientObjectSocketFactory (inetAddress: InetAddress, port: Int) extends ObjectSocketFactory {

  def connect(): Either[String, ObjectSocketConnection] = {
    ObjectSocketConnection(inetAddress, port) match {
      case Success(con) => Right(con)
      case Failure(err) => Left(s"Unable to open connection [$err]")
    }
  }

}


package protocol.client2

import com.typesafe.scalalogging.LazyLogging

class ConnectionManager(factory: ClientConnectionFactory) extends LazyLogging {

  private var currentConnection: Option[ClientConnection] = None

  def getConnection: Either[String, ClientConnection] = {
    currentConnection match {
      case Some(conn) => Right(conn)
      case None =>
        factory.connect() match {
          case Right(conn) =>
            currentConnection = Some(conn)
            Right(conn)

          case Left(err) => Left(err)
        }
    }
  }

  def closeConnection(): Option[Throwable] = {
    val closeResult = currentConnection.flatMap { _.close() }
    currentConnection = None
    closeResult
  }

}

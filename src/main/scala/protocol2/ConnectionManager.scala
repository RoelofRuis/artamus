package protocol2

import com.typesafe.scalalogging.LazyLogging

class ConnectionManager(factory: ObjectSocketFactory) extends LazyLogging {

  private var currentConnection: Option[ObjectSocketConnection] = None

  def getConnection: Either[String, ObjectSocketConnection] = {
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

package protocol.client2

import com.typesafe.scalalogging.LazyLogging

class RetryConnection (
  inner: ClientConnectionFactory,
  retrySleep: Int
) extends ClientConnectionFactory with LazyLogging {

  def connect(): Either[String, ClientConnection] = {
    inner.connect() match {
      case Right(conn) => Right(conn)
      case Left(err) =>
        logger.info(s"Unable to connect [$err]\nRetrying in [$retrySleep]")
        Thread.sleep(retrySleep)
        connect()
    }
  }

}


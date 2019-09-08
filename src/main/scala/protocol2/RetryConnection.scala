package protocol2

import com.typesafe.scalalogging.LazyLogging

class RetryConnection (
  inner: ObjectSocketFactory,
  retrySleep: Int
) extends ObjectSocketFactory with LazyLogging {

  def connect(): Either[String, ObjectSocketConnection] = {
    inner.connect() match {
      case Right(conn) => Right(conn)
      case Left(err) =>
        logger.info(s"Unable to connect [$err]\nRetrying in [$retrySleep]")
        Thread.sleep(retrySleep)
        connect()
    }
  }

}


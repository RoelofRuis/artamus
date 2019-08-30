package protocol

import java.io.ObjectInputStream

import protocol.MessageTypes._

import scala.util.{Failure, Success, Try}

private[protocol] class ClientInputStream(in: ObjectInputStream, eventRegistry: ClientEventRegistry) {

  def expectResponseMessage[A]: Either[StreamException, A] = {
    readObject[ServerResponse]().toEither match {
      case Right(DataResponse) =>
        readObject[Either[String,A]]() match {
          case Success(Right(obj)) => Right(obj)
          case Success(Left(serverError)) => Left(s"Server Error: $serverError")
          case Failure(ex) => Left(s"Unable to decode Data message. [$ex]")
        }

      case Right(EventResponse) =>
        // TODO: clean up handling of events, move to separate handler
        val x = readObject[Event]().flatMap(e => Try(eventRegistry.publish(e)))
        if (x.isFailure) x.failed.get.printStackTrace()

        expectResponseMessage

      case Left(ex) => Left(s"Unable to determine message type. [$ex]")
    }
  }

  private def readObject[A](): Try[A] = Try(in.readObject().asInstanceOf[A])

}

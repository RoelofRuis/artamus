package protocol.client

import java.io.ObjectInputStream

import protocol.Event
import protocol.MessageTypes._

import scala.util.{Failure, Success, Try}

private[protocol] class ClientInputStream(in: ObjectInputStream) {

  def expectResponseMessage[A]: (Either[StreamException, A], List[Try[Event]]) = readResponsesAndEvents(List())

  private def readResponsesAndEvents[A](events: List[Try[Event]]): (Either[StreamException, A], List[Try[Event]]) = {
    readObject[ServerResponse]().toEither match {
      case Right(DataResponse) =>
        val response = readObject[Either[String,A]]() match {
          case Success(Right(obj)) => Right(obj)
          case Success(Left(serverError)) => Left(s"Server Error: $serverError")
          case Failure(ex) => Left(s"Unable to decode Data message. [$ex]")
        }
        (response, events)

      case Right(EventResponse) => readResponsesAndEvents(events :+ readObject[Event]())

      case Left(ex) => (Left(s"Unable to determine message type. [$ex]"), events)
    }
  }

  private def readObject[A](): Try[A] = Try(in.readObject().asInstanceOf[A])

}

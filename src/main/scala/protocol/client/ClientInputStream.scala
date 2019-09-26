package protocol.client

import java.io.ObjectInputStream

import protocol._

import scala.util.{Failure, Success, Try}

private[protocol] class ClientInputStream(in: ObjectInputStream) {

  def expectResponseMessage[A]: (Either[ServerException, A], List[Try[Event]]) = readResponsesAndEvents(List())

  private def readResponsesAndEvents[A](events: List[Try[Event]]): (Either[ServerException, A], List[Try[Event]]) = {
    decode[ServerResponse](in.readObject).toEither match {
      case Right(DataResponse(response)) =>
        val decoded = response match {
          case Left(serverError) => Left(s"Server Error: $serverError")
          case Right(data) =>
            decode[A](data) match {
              case Success(obj) => Right(obj)
              case Failure(ex) => Left(s"Unable to decode Data message. [$ex]")
            }
        }
        (decoded, events)

      case Right(EventResponse(e)) => readResponsesAndEvents(events :+ decode[Event](e))
      case Left(ex) => (Left(s"Could not read server response. [$ex]"), events)
    }
  }

  private def decode[A](in: Any): Try[A] = Try(in.asInstanceOf[A])

}

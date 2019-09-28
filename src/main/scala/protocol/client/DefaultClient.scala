package protocol.client

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import protocol._
import pubsub.Dispatcher
import resource.Resource

import scala.util.{Failure, Success, Try}

class DefaultClient @Inject() (
  client: Resource[SimpleClient],
  eventDispatcher: Dispatcher[Event],
) extends ClientInterface with LazyLogging {

  override def sendCommand[A <: protocol.Command](message: A): Option[protocol.Command#Res] = {
    sendRequest[CommandRequest, A#Res](CommandRequest(message))
  }

  override def sendQuery[A <: protocol.Query](message: A): Option[A#Res] = {
    sendRequest[QueryRequest, A#Res](QueryRequest(message))
  }

  def close(): Unit = client.close

  private def sendRequest[A, R](request: A): Option[R] = {
    client.acquire match {
      case Right(c) =>
        logger.debug(s"Send request [$request]")
        c.send(request)
        val (response, events) = expectResponseMessage[R](c)
        logger.debug(s"Received events [$events]")
        handleEvents(events)
        logger.debug(s"Received response [$response]")
        response.toOption

      case Left(ex) =>
        logger.error("Client encountered connection exception", ex)
        None
    }
  }

  private def expectResponseMessage[A](client: SimpleClient): (Either[ServerException, A], List[Try[Event]]) = readResponsesAndEvents(client, List())

  private def readResponsesAndEvents[A](client: SimpleClient, events: List[Try[Event]]): (Either[ServerException, A], List[Try[Event]]) = {
    decode[ServerResponse](client.readNext).toEither match {
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

      case Right(EventResponse(e)) => readResponsesAndEvents(client, events :+ decode[Event](e))
      case Left(ex) => (Left(s"Could not read server response. [$ex]"), events)
    }
  }

  private def decode[A](in: Any): Try[A] = Try(in.asInstanceOf[A])

  private def handleEvents(events: List[Try[Event]]): Unit = events.foreach(_.foreach(eventDispatcher.handle))
}


package protocol

import java.util.concurrent.SynchronousQueue

import com.typesafe.scalalogging.LazyLogging
import pubsub.Dispatcher
import resource.Resource
import transport.client.ClientThread

import scala.util.{Failure, Success, Try}

class DefaultClient(
  client: Resource[ClientThread],
  eventDispatcher: Dispatcher[Event],
) extends ClientInterface with LazyLogging {

  private val dataQueue: SynchronousQueue[DataResponse] = new SynchronousQueue[DataResponse]()

  private val receiveThread: Thread = new Thread() {
    override def run(): Unit = {
      while (! Thread.currentThread().isInterrupted) {
        client.acquire match {
          case Right(c) =>
            decode[ServerResponse](c.readNext).toEither match {
              case Right(d @ DataResponse(_)) => dataQueue.put(d)
              case Right(EventResponse(event)) => eventDispatcher.handle(event) // TODO: separate by queue!
              case Left(ex) => logger.error(s"Could not read server response.", ex)
            }

          case Left(ex) =>
            logger.error("Client encountered connection exception", ex)
            Thread.currentThread().interrupt()
        }
      }
    }
  }

  override def open(): Unit = receiveThread.start()

  override def sendCommand[A <: protocol.Command](message: A): Option[protocol.Command#Res] = {
    sendRequest[CommandRequest, A#Res](CommandRequest(message))
  }

  override def sendQuery[A <: protocol.Query](message: A): Option[A#Res] = {
    sendRequest[QueryRequest, A#Res](QueryRequest(message))
  }

  override def close(): Unit = {
    receiveThread.interrupt()
    client.close
  }

  private def sendRequest[A, R](request: A): Option[R] = {
    client.acquire match {
      case Right(c) =>
        logger.debug(s"Send request [$request]")
        c.send(request)
        val response: DataResponse = dataQueue.take()
        logger.debug(s"Received response [$response]")
        response.data match {
          case Left(serverError) =>
            logger.warn(s"server error [$serverError]")
            None

          case Right(data) =>
            decode[R](data) match {
              case Success(obj) =>
                Some(obj)
              case Failure(ex) =>
                logger.warn(s"Unable to decode message", ex)
                None
            }
        }

      case Left(ex) =>
        logger.error("Client encountered connection exception", ex)
        None
    }
  }

  private def decode[A](in: Any): Try[A] = Try(in.asInstanceOf[A])

}

object DefaultClient {

  def apply(port: Int, dispatcher: Dispatcher[Event]): ClientInterface = {
    new DefaultClient(ClientThread.asResource(port), dispatcher)
  }

}
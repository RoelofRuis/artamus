package protocol

import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}

import com.typesafe.scalalogging.LazyLogging
import pubsub.{DispatchThread, Dispatcher}
import resource.Resource
import transport.client.ClientThread

import scala.util.{Failure, Success, Try}

class DefaultClient(
  client: Resource[ClientThread],
  eventDispatcher: Dispatcher[Event],
) extends ClientInterface with LazyLogging {

  private val eventQueue: BlockingQueue[Event] = new ArrayBlockingQueue[Event](64)

  private val eventThread: Thread = new DispatchThread[Event](eventQueue, eventDispatcher)
  private val receiveThread: ReceiveThread = new ReceiveThread(client, eventQueue)

  override def open(): Unit = {
    eventThread.start()
    receiveThread.start()
  }

  override def sendCommand[A <: protocol.Command](message: A): Option[protocol.Command#Res] = {
    sendRequest[CommandRequest, A#Res](CommandRequest(message))
  }

  override def sendQuery[A <: protocol.Query](message: A): Option[A#Res] = {
    sendRequest[QueryRequest, A#Res](QueryRequest(message))
  }

  override def close(): Unit = {
    eventThread.interrupt()
    receiveThread.interrupt()
    client.close
  }

  private def sendRequest[A, R](request: A): Option[R] = {
    client.acquire match {
      case Right(c) =>
        logger.debug(s"Send request [$request]")
        // TODO: push this logic to the receiver thread!
        receiveThread.expect()
        c.send(request)
        val response: DataResponse = receiveThread.take()
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
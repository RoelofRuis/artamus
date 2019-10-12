package protocol

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.{BlockingQueue, SynchronousQueue}

import com.typesafe.scalalogging.LazyLogging
import resource.Resource
import transport.client.ClientThread

import scala.util.{Failure, Success, Try}

class ClientMessagingThread(
  eventQueue: BlockingQueue[Event],
  client: Resource[ClientThread]
) extends Thread with LazyLogging {

  private val queue: SynchronousQueue[DataResponse] = new SynchronousQueue[DataResponse]()
  private val expectsData: AtomicBoolean = new AtomicBoolean(false)

  def sendRequest[A, R](request: A): Option[R] = {
    client.acquire match {
      case Right(c) =>
        expectsData.set(true)
        c.send(request)
        val response = queue.take()
        expectsData.set(false)
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

  override def run(): Unit = {
    try {
      while (! Thread.currentThread().isInterrupted) {
        client.acquire match {
          case Right(c) =>
            decode[ServerResponse](c.readNext).toEither match {
              case Right(d @ DataResponse(_)) if expectsData.get() => queue.put(d)
              case Right(d @ DataResponse(_)) => logger.error(s"Received unexpected data response [$d]")
              case Right(e @ EventResponse(_)) => eventQueue.put(e.event)
              case Left(ex) =>
                logger.error(s"Could not read server response.", ex)
                Thread.currentThread().interrupt()
            }

          case Left(ex) =>
            logger.error("Client encountered connection exception", ex)
            Thread.currentThread().interrupt()
        }
      }
    } catch {
      case _: InterruptedException =>
        client.close
        Thread.currentThread().interrupt()
    }
  }

  private def decode[A](in: Any): Try[A] = Try(in.asInstanceOf[A])

}

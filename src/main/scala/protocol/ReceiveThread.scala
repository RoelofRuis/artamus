package protocol

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.{BlockingQueue, SynchronousQueue}

import com.typesafe.scalalogging.LazyLogging
import resource.Resource
import transport.client.ClientThread

import scala.util.Try

class ReceiveThread(
  client: Resource[ClientThread],
  eventQueue: BlockingQueue[Event]
) extends Thread with LazyLogging {

  private val queue: SynchronousQueue[DataResponse] = new SynchronousQueue[DataResponse]()
  private val expectsData: AtomicBoolean = new AtomicBoolean(false)

  def expect(): Unit = expectsData.set(true)

  def take(): DataResponse = {
    val res = queue.take()
    expectsData.set(false)
    res
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
        Thread.currentThread().interrupt()
    }
  }

  private def decode[A](in: Any): Try[A] = Try(in.asInstanceOf[A])

}

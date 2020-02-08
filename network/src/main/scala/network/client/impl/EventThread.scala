package network.client.impl

import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}

import network.EventResponseMessage
import network.client.api.ClientEventHandler

import scala.util.{Failure, Success, Try}

private[client] final class EventThread[E](
  eventHandler: ClientEventHandler[E]
) extends Thread with EventScheduler {

  private val queue: BlockingQueue[EventResponseMessage[_]] = new ArrayBlockingQueue[EventResponseMessage[_]](64)

  def schedule(eventResponseMessage: EventResponseMessage[_]): Try[Unit] = Try { queue.put(eventResponseMessage) }

  override def run(): Unit = {
    try {
      while (! Thread.currentThread().isInterrupted) {
        val elem = queue.take()
        Try(elem.event.asInstanceOf[E]) match {
          case Success(e) => eventHandler.handleEvent(e)
          case Failure(ex) => eventHandler.receivedInvalidEvent(ex)
        }
      }
    } catch {
      case _: InterruptedException =>
    }
  }

}

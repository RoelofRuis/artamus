package protocol.client.impl

import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}

import protocol.Event
import protocol.client.api.EventDispatcher

private[client] final class EventThread(
  dispatcher: EventDispatcher
) extends Thread with EventScheduler {

  // TODO: improve error handling of `queue.put`

  def schedule(event: Event): Unit = queue.put(event)

  private val queue: BlockingQueue[Event] = new ArrayBlockingQueue[Event](64)

  override def run(): Unit = {
    try {
      while (! Thread.currentThread().isInterrupted) {
        dispatcher.dispatch(queue.take())
      }
    } catch {
      case _: InterruptedException =>
    }
  }

}

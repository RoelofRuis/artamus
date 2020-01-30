package protocol.client.impl

import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}

import protocol.Event
import protocol.client.api.EventDispatcher

private[client] final class EventThread(
  dispatcher: EventDispatcher
) extends Thread with EventScheduler {

  private val queue: BlockingQueue[Event] = new ArrayBlockingQueue[Event](64)

  // TODO: improve error handling of `queue.put`
  def schedule(event: Event): Unit = queue.put(event)

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

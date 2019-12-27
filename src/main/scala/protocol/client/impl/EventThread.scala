package protocol.client.impl

import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}

import protocol.Event
import pubsub.{Callback, Dispatcher}

class EventThread(
  dispatcher: Dispatcher[Callback, Event]
) extends Thread with EventScheduler {

  // TODO: improve error handling of `dispacher.handle` and `queue.put`

  def schedule(event: Event): Unit = queue.put(event)

  private val queue: BlockingQueue[Event] = new ArrayBlockingQueue[Event](64)

  override def run(): Unit = {
    try {
      while (! Thread.currentThread().isInterrupted) {
        val callback = Callback(queue.take())
        dispatcher.handle(callback)
      }
    } catch {
      case _: InterruptedException =>
    }
  }

}

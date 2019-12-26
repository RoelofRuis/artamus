package protocol.v2.client.impl

import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}

import protocol.v2.Event2
import pubsub.{Callback, Dispatcher}

class EventThread(
  dispatcher: Dispatcher[Callback, Event2]
) extends Thread with EventScheduler {

  // TODO: improve error handling of `dispacher.handle` and `queue.put`

  def schedule(event: Event2): Unit = queue.put(event)

  private val queue: BlockingQueue[Event2] = new ArrayBlockingQueue[Event2](64)

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

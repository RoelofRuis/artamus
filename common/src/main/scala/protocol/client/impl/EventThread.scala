package protocol.client.impl

import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}

import protocol.client.api.EventDispatcher

private[client] final class EventThread[E](
  dispatcher: EventDispatcher[E]
) extends Thread with EventScheduler[E] {

  private val queue: BlockingQueue[E] = new ArrayBlockingQueue[E](64)

  // TODO: improve error handling of `queue.put`
  def schedule(event: E): Unit = queue.put(event)

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

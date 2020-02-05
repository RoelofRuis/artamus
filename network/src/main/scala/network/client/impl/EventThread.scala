package network.client.impl

import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}

import network.client.api.ClientAPI

private[client] final class EventThread[E](
  api: ClientAPI[E]
) extends Thread with EventScheduler[E] {

  private val queue: BlockingQueue[E] = new ArrayBlockingQueue[E](64)

  // TODO: improve error handling of `queue.put`
  def schedule(event: E): Unit = queue.put(event)

  override def run(): Unit = {
    try {
      while (! Thread.currentThread().isInterrupted) {
        api.receivedEvent(queue.take())
      }
    } catch {
      case _: InterruptedException =>
    }
  }

}

package pubsub

import java.util.concurrent.BlockingQueue

import scala.reflect.ClassTag

class DispatchThread[A <: { type Res } : ClassTag](
  eventQueue: BlockingQueue[A],
  dispatcher: Dispatcher[A]
) extends Thread {
  override def run(): Unit = {
    try {
      while (! Thread.currentThread().isInterrupted) {
        dispatcher.handle(eventQueue.take())
      }
    } catch {
      case _: InterruptedException =>
    }
  }
}
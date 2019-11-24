package pubsub

import java.util.concurrent.BlockingQueue

import scala.reflect.ClassTag

class DispatchThread[A <: { type Res } : ClassTag](
  eventQueue: BlockingQueue[A],
  dispatcher: Dispatcher[Callback, A]
) extends Thread {
  override def run(): Unit = {
    try {
      while (! Thread.currentThread().isInterrupted) {
        val callback = Callback(eventQueue.take())
        dispatcher.handle(callback)
      }
    } catch {
      case _: InterruptedException =>
    }
  }
}
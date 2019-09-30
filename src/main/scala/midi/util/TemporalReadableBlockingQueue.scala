package midi.util

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import javax.annotation.concurrent.GuardedBy
import midi.util.TemporalReadableBlockingQueue.BlockingQueueReadMethod

import scala.language.higherKinds

final class TemporalReadableBlockingQueue[A]() {

  @GuardedBy("this") private var queue: Option[LinkedBlockingQueue[A]] = None

  def read[L[_]](readMethod: BlockingQueueReadMethod[A, L]): L[A] = {
    val currentQueue = new LinkedBlockingQueue[A]()
    this.synchronized { queue = Some(currentQueue) }
    val result = readMethod(currentQueue)
    this.synchronized { queue = None }
    result
  }

  def offer(elem: A): Unit = this.synchronized {
    queue.map(_.offer(elem))
  }

}

object TemporalReadableBlockingQueue {

  type BlockingQueueReadMethod[A, L[_]] = BlockingQueue[A] => L[A]

}

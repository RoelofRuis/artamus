package midi.util

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import midi.util.TemporalReadableBlockingQueue.BlockingQueueReadMethod

import scala.language.higherKinds

final class TemporalReadableBlockingQueue[A]() {

  private var queue: Option[LinkedBlockingQueue[A]] = None

  def read[L[_]](readMethod: BlockingQueueReadMethod[A, L]): L[A] = {
    val currentQueue = new LinkedBlockingQueue[A]()
    queue = Some(currentQueue)
    val result = readMethod(currentQueue)
    queue = None
    result
  }

  // TODO: investigate whether this is truly thread safe
  def offer(elem: A): Unit = queue.map(_.offer(elem))

}

object TemporalReadableBlockingQueue {

  type BlockingQueueReadMethod[A, L[_]] = BlockingQueue[A] => L[A]

}

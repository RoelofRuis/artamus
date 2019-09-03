package client.midi.util

import java.util.concurrent.LinkedBlockingQueue

import client.midi.util.BlockingQueueReader.BlockingQueueReadMethod

import scala.language.higherKinds

final class TimedBlockingQueueReader[A]() extends BlockingQueueReader[A] {

  private var queue: Option[LinkedBlockingQueue[A]] = None

  def read[L[_]](readMethod: BlockingQueueReadMethod[A, L]): L[A] = {
    val currentQueue = new LinkedBlockingQueue[A]()
    queue = Some(currentQueue)
    val result = readMethod(currentQueue)
    queue = None
    result
  }

  // TODO: investigate whether this is truly thread safe
  def write(elem: A): Unit = queue.map(_.offer(elem))

  override def close(): Unit = ()

}

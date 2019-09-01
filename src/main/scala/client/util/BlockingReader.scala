package client.util

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import scala.language.higherKinds

final class BlockingReader[A]() {

  private var queue: Option[LinkedBlockingQueue[A]] = None

  def read[L[_]](readMethod: BlockingQueue[A] => L[A]): L[A] = {
    val currentQueue = new LinkedBlockingQueue[A]()
    queue = Some(currentQueue)
    val result = readMethod(currentQueue)
    queue = None
    result
  }

  // TODO: investigate whether this is truly thread safe
  def write(elem: A): Unit = queue.map(_.offer(elem))

}

package client.util

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import client.util.BlockingReader.ReadMethod

import scala.language.higherKinds

final class BlockingReader[A]() {

  private var queue: Option[LinkedBlockingQueue[A]] = None

  def read[L[_]](readMethod: ReadMethod[A, L]): L[A] = {
    val currentQueue = new LinkedBlockingQueue[A]()
    queue = Some(currentQueue)
    val result = readMethod(currentQueue)
    queue = None
    result
  }

  // TODO: investigate whether this is truly thread safe
  def write(elem: A): Unit = queue.map(_.offer(elem))

}

object BlockingReader {

  type ReadMethod[A, L[_]] = BlockingQueue[A] => L[A]

}
package client.util

import java.util.concurrent.BlockingQueue

import client.util.BlockingQueueReader.BlockingQueueReadMethod

import scala.language.higherKinds

trait BlockingQueueReader[A] {

  def read[L[_]](readMethod: BlockingQueueReadMethod[A, L]): L[A]

  def close(): Unit = ()

}

object BlockingQueueReader {

  type BlockingQueueReadMethod[A, L[_]] = BlockingQueue[A] => L[A]

}
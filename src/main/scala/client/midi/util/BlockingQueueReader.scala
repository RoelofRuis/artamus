package client.midi.util

import java.util.concurrent.BlockingQueue

import client.midi.util.BlockingQueueReader.BlockingQueueReadMethod

import scala.language.higherKinds

// TODO: determine all dependencies on this class!!
trait BlockingQueueReader[A] {

  def read[L[_]](readMethod: BlockingQueueReadMethod[A, L]): L[A]

}

object BlockingQueueReader {

  type BlockingQueueReadMethod[A, L[_]] = BlockingQueue[A] => L[A]

}
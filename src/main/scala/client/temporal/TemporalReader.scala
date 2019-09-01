package client.temporal

import java.util.concurrent.BlockingQueue

import scala.language.higherKinds

class TemporalReader[A](readable: TemporalReadable[A]) {

  def read[L[_]](readMethod: BlockingQueue[A] => L[A]): L[A] = {
    val queue = readable.startReading()
    val result = readMethod(queue)
    readable.stopReading()
    result
  }

}

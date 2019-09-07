package midi.util

import midi.util.TemporalReadableBlockingQueue.BlockingQueueReadMethod

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

object ReadIntoList {

  def untilEnter[A]: BlockingQueueReadMethod[A, List] = { queue =>
    System.in.read()
    val buffer = new ListBuffer[A]
    queue.drainTo(buffer.asJava)
    buffer.toList
  }

  def take[A](n: Int): BlockingQueueReadMethod[A, List] = { queue =>
    Range(0, n).map(_ => queue.take()).toList
  }

  def takeFiltered[A](n: Int, filter: A => Boolean): BlockingQueueReadMethod[A, List] = { queue =>
    def loop(acc: List[A]): List[A] = {
      if (acc.size == n) acc
      else {
        val next = queue.take()
        if ( ! filter(next)) loop(acc)
        else loop(acc :+ next)
      }
    }

    loop(List())
  }

}

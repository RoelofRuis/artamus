package midi.util

import com.typesafe.scalalogging.LazyLogging
import midi.util.TemporalReadableBlockingQueue.BlockingQueueReadMethod

@deprecated
private[midi] object ReadIntoList extends LazyLogging {

  def take[A](n: Int): BlockingQueueReadMethod[A, List] = takeUntil(_.length == n, _ => true)

  def takeFiltered[A](n: Int, accept: A => Boolean): BlockingQueueReadMethod[A, List] = takeUntil(_.length == n, accept)

  def takeUntil[A](condition: List[A] => Boolean, accept: A => Boolean): BlockingQueueReadMethod[A, List] = { queue =>
    def take: A = {
      val next = queue.take()
      logger.debug(s"MIDI in received [$next]")
      if (accept(next)) next
      else take
    }

    def loop(acc: List[A]): List[A] = {
      if (condition(acc)) acc
      else loop(acc :+ take)
    }

    loop(List())
  }

}

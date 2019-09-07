package midi.util

import midi.util.TemporalReadableBlockingQueue.BlockingQueueReadMethod

object ReadIntoList {

  def take[A](n: Int): BlockingQueueReadMethod[A, List] = takeUntil(_.length == n, _ => true)

  def takeFiltered[A](n: Int, accept: A => Boolean): BlockingQueueReadMethod[A, List] = takeUntil(_.length == n, accept)

  def takeUntil[A](condition: List[A] => Boolean, accept: A => Boolean): BlockingQueueReadMethod[A, List] = { queue =>
    def take: A = {
      val next = queue.take()
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

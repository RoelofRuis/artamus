package nl.roelofruis.artamus.core.common

object Temporal {

  final case class Windowed[A](window: Window, element: A) {
    val get: A = element
  }

  object Windowed {
    def apply[A](position: Position, duration: Duration, element: A): Windowed[A] = {
      Windowed(Window(position, duration), element)
    }
  }

  type Timeline[A] = Seq[Windowed[A]]

  object Timeline {
    def empty[A]: Timeline[A] = Seq.empty
  }

  implicit class TimelineOps[A](seq: Timeline[A]) {
    def mapVal[B](f: A => B): Timeline[B] = seq.map(w => w.copy(element=f(w.get)))
  }

  final case class Positioned[A](position: Position, element: A) {
    val get: A = element
  }

  final case class TemporalValue[A](head: Positioned[A], tail: Seq[Positioned[A]]) {
    def asSeq: Seq[Positioned[A]] = head +: tail
    def last: Positioned[A] = if (tail.isEmpty) head else tail.last
    def :+(elem: Positioned[A]): TemporalValue[A] = TemporalValue(head, tail :+ elem)
  }

  object TemporalValue {
    def apply[A](a: A): TemporalValue[A] = TemporalValue(Positioned(Position.ZERO, a), Seq.empty)
  }

  trait ProvidesDuration[A] {
    def duration(a: A): Duration
  }

  implicit class TemporalValWithDurationOps[A](value: TemporalValue[A])(implicit val durationProvider: ProvidesDuration[A]) {
    def iterateWindowed: LazyList[Windowed[A]] = {
      val active = value.head.get

      def loop(searchPos: Position): LazyList[Windowed[A]] = {
        // TODO: make lookup actually work..!
        val duration = durationProvider.duration(active)
        Windowed(searchPos, duration, active) #:: loop(searchPos + duration)
      }

      loop(Position.ZERO)
    }
  }

}

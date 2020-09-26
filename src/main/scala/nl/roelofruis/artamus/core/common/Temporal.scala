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

  type WindowedSeq[A] = Seq[Windowed[A]]

  object WindowedSeq {
    def empty[A]: WindowedSeq[A] = Seq.empty
  }

  final case class Positioned[A](position: Position, element: A) {
    val get: A = element
  }

  final case class TemporalVal[A](head: Positioned[A], tail: Seq[Positioned[A]]) {
    def asSeq: Seq[Positioned[A]] = head +: tail
  }

  object TemporalVal {
    def apply[A](a: A): TemporalVal[A] = TemporalVal(Positioned(Position.ZERO, a), Seq.empty)
  }

}

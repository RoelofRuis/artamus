package nl.roelofruis.artamus.core.common

object Containers {

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

  type PositionedSeq[A] = Seq[Positioned[A]]

  object PositionedSeq {
    def startingWith[A](a: A): PositionedSeq[A] = Seq(Positioned(Position.ZERO, a))
  }

}

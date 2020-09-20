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

    def startingWithInstant[A](a: A): WindowedSeq[A] = Seq(Windowed(Position.ZERO, Duration.ZERO, a))
  }

  implicit class WindowedSeqOps[A](seq: WindowedSeq[A]) {
    val duration: Duration = seq.foldRight(Duration.ZERO) { case (w, acc) => acc + w.window.duration }
  }

}

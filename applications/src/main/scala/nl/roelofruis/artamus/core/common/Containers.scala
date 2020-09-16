package nl.roelofruis.artamus.core.common

import scala.collection.immutable.SortedMap

object Containers {

  /** Represents an element that has an associated window, meaning it has a finite and positioned duration in time. */
  final case class Windowed[A](window: Window, element: A)

  object Windowed {
    def apply[A](position: Position, duration: Duration, element: A): Windowed[A] = {
      Windowed(Window(position, duration), element)
    }
  }

  /** Represents an element that has an associated position, meaning it is sits at an instant in time. */
  final case class Positioned[A](position: Position, element: A)

  type TemporalMap[A] = SortedMap[Position, Windowed[A]]

  object TemporalMap {
    def fromSequence[A](seq: Seq[Windowed[A]]): TemporalMap[A] = {
      seq.foldLeft(SortedMap[Position, Windowed[A]]()) {
        case (acc, windowed) => acc.updated(windowed.window.start, windowed)
      }
    }
  }

  type TemporalInstantMap[A] = SortedMap[Position, A]

  object TemporalInstantMap {
    def startingWith[A](a: A): TemporalInstantMap[A] = fromSequence(Seq(Positioned(Position.ZERO, a)))

    def fromSequence[A](seq: Seq[Positioned[A]]): TemporalInstantMap[A] = {
      seq.foldLeft(SortedMap[Position, A]()) {
        case (acc, positioned) => acc.updated(positioned.position, positioned.element)
      }
    }
  }

}

package nl.roelofruis.artamus.core

import nl.roelofruis.artamus.core.primitives.{Duration, Position, Window}

object Containers {

  final case class Windowed[A](window: Window, element: A)

  object Windowed {

    def apply[A](position: Position, duration: Duration, element: A): Windowed[A] = {
      Windowed(Window(position, duration), element)
    }

  }

}

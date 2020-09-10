package nl.roelofruis.artamus.core.primitives

/**
 * Represents any element positioned in time.
 */
final case class Windowed[A](window: Window, element: A)

object Windowed {

  def apply[A](position: Position, duration: Duration, element: A): Windowed[A] = {
    Windowed(Window(position, duration), element)
  }

}

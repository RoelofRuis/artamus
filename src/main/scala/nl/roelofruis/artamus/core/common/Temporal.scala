package nl.roelofruis.artamus.core.common

import nl.roelofruis.artamus.core.common.Temporal.Positioned

final case class Temporal[A : Positioned](initialValue: A, tail: Seq[A])

object Temporal {

  final case class PositionedValue[A](a: A, position: Position) extends Positioned[A]

  trait Positioned[A] {
    val position: Position
  }

}

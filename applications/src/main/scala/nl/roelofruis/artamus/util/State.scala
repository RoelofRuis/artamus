package nl.roelofruis.artamus.util

case class State[S, +A](run: S => (S, A)) {

  def map[B](f: A => B): State[S, B] =
    flatMap(a => State.unit(f(a)))

  def flatMap[B](f: A => State[S, B]): State[S, B] =
    State { s =>
      val (s1, a) = run(s)
      f(a).run(s1)
    }
}

object State {

  def unit[S, A](a: => A): State[S, A] =
    State { s => (s, a) }

}
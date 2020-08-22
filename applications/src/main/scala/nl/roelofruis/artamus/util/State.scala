package nl.roelofruis.artamus.util

import nl.roelofruis.artamus.util.State.Result

case class State[S, +A](run: S => Result[S, A]) {

  def map[B](f: A => B): State[S, B] =
    flatMap(a => State.unit(f(a)))

  def flatMap[B](f: A => State[S, B]): State[S, B] =
    State { s =>
      val result = run(s)
      f(result.value).run(result.state)
    }
}

object State {

  def unit[S, A](a: => A): State[S, A] =
    State { s => (s, a) }

  case class Result[S, +A](state: S, value: A)

  implicit def tupleAsResult[S, A](t: (S, A)): Result[S, A] = Result(t._1, t._2)

}
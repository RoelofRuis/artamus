package artamus.core.ops.edit.analysis

import artamus.core.ops.edit.analysis.Interpretation.{AllOf, OneOf}

/**
  * Models an interpretation of some phenomenon as a disjunction of conjunctions.
  *
  * For example the following phenomenon:
  * ((A & B) | (C & D))
  * That can be interpreted as "either 'A and B' or 'C and D'"
  *
  * Combinators exist on the Interpretation class to transition from one interpretation to another.
  *
  * TODO: make evaluation lazy so it can be more memory efficient
  */
final case class Interpretation[A] private (data: OneOf[AllOf[A]]) {

  /**
    * Each element of type A can be interpreted as multiple elements of type B
    */
  def expand[B](f: A => Set[B]): Interpretation[B] = {
    val expanded = data
      .map { all: AllOf[A] => expandAllOf[B](all)(f(_)) }
      .reduce(_ ++ _)
    Interpretation(expanded)
  }

  /**
    * Each element of type A can be interpreted as exactly 1 element of type B
    */
  def map[B](f: A => B): Interpretation[B] = {
    Interpretation(data.map((all: AllOf[A]) => all.map((a: A) => f(a))))
  }

  /**
    * Each element of type A can be interpreted as at most 1 element of type B
    */
  def mapOption[B](f: A => Option[B]): Interpretation[B] = {
    Interpretation(data.map((all: AllOf[A]) => all.flatMap((a: A) => f(a))).filter(_.nonEmpty))
  }

  /**
    * Multiple elements of type A occurring together can be interpreted as at most 1 element of type B
    */
  def mapAll[B](f: Set[A] => Option[B]): Interpretation[B] = {
    Interpretation.oneOf(data.flatMap((all: AllOf[A]) => f(all)))
  }

  def filter(f: Set[A] => Boolean): Interpretation[A] = {
    Interpretation(data.filter(f))
  }

  def isEmpty: Boolean = data.headOption.getOrElse(Nil).isEmpty

  override def toString: String = data.map(_.mkString(" and ")).map("(" + _ + ")").mkString(" or ")

  private def combineLists[T](l1: Set[Set[T]], l2: Set[T]): Set[Set[T]] = {
    if (l2.isEmpty) l1
    else l2.flatMap((elem: T) => l1.map((list: Set[T]) => list + elem))
  }

  private def expandAllOf[T](a: AllOf[A])(f: A => Set[T]): OneOf[AllOf[T]] = {
    a.map(f).foldLeft(Set(Set[T]()))((res, elem) => combineLists[T](res, elem))
  }
}

object Interpretation {

  private type OneOf[A] = Set[A]
  private type AllOf[A] = Set[A]

  /* Creators */
  def empty[A]: Interpretation[A] = Interpretation(Set())

  def only[A](a: A): Interpretation[A] = Interpretation(Set(Set(a)))

  def oneOf[A](l: Set[A]): Interpretation[A] = Interpretation(l.map(Set(_)))

  def allOf[A](l: Set[A]): Interpretation[A] = Interpretation(Set(l))

}

package server.analysis

import server.analysis.Interpretation.{AllOf, OneOf}

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
  def expand[B](f: A => Seq[B]): Interpretation[B] = {
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
  def mapAll[B](f: Seq[A] => Option[B]): Interpretation[B] = {
    Interpretation.oneOf(data.flatMap((all: AllOf[A]) => f(all)))
  }

  def filter(f: Seq[A] => Boolean): Interpretation[A] = {
    Interpretation(data.filter(f))
  }

  def distinct: Interpretation[A] = {
    Interpretation(data.map((all: AllOf[A]) => all.distinct))
  }

  def isEmpty: Boolean = data.headOption.getOrElse(Nil).isEmpty

  override def toString: String = data.map(_.mkString(" and ")).map("(" + _ + ")").mkString(" or ")

  private def combineLists[T](l1: Seq[Seq[T]], l2: Seq[T]): Seq[Seq[T]] = {
    if (l2.isEmpty) l1
    else l2.flatMap((elem: T) => l1.map((list: Seq[T]) => elem +: list))
  }

  private def expandAllOf[T](a: AllOf[A])(f: A => Seq[T]): OneOf[AllOf[T]] = {
    a.map(f).foldLeft(Seq(Seq[T]()))((res, elem) => combineLists[T](res, elem))
  }
}

object Interpretation {

  private type OneOf[A] = Seq[A]
  private type AllOf[A] = Seq[A]

  /* Creators */
  def empty[A]: Interpretation[A] = Interpretation(Seq())

  def only[A](a: A): Interpretation[A] = Interpretation(Seq(Seq(a)))

  def oneOf[A](l: Seq[A]): Interpretation[A] = Interpretation(l.map(Seq(_)))

  def allOf[A](l: Seq[A]): Interpretation[A] = Interpretation(Seq(l))

}

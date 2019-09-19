package music.interpret

import music.interpret.Interpretation.{AllOf, OneOf}

/**
  * Models an interpretation of some phenomenon as a disjunction of conjunctions.
  *
  * For example the following phenomenon:
  * ((A & B) | (C & D))
  * That can be interpreted as "either 'A and B' or 'C and D'"
  *
  * Combinators exist on the Interpretation class to transition from one interpretation to another.
  */
object Interpretation {

  private type OneOf[A] = List[A]
  private type AllOf[A] = List[A]

  /* Creators */
  def empty[A]: Interpretation[A] = Interpretation(Nil)

  def only[A](a: A): Interpretation[A] = Interpretation((a :: Nil) :: Nil)

  def oneOf[A](l: List[A]): Interpretation[A] = Interpretation(l.map((a: A) => a :: Nil))

  def allOf[A](l: List[A]): Interpretation[A] = Interpretation(l :: Nil)

}

case class Interpretation[A](data: OneOf[AllOf[A]]) {

  def add(other: Interpretation[A]) = Interpretation(data ::: other.data)

  /**
    * Each element of type A can be interpreted as multiple elements of type B
    */
  def expand[T](f: A => List[T]): Interpretation[T] = {
    data.map((all: AllOf[A]) => expandAllOf[T](all)(f)).reduce(_.add(_))
  }

  /**
    * Each element of type A can be interpreted as exactly 1 element of type B
    */
  def map[T](f: A => T): Interpretation[T] = {
    Interpretation(data.map((all: AllOf[A]) => all.map((a: A) => f(a))))
  }

  /**
    * Multiple elements of type A occurring together can be interpreted as at most 1 element of type B
    */
  def mapAll[T](f: List[A] => Option[T]): Interpretation[T] = {
    Interpretation.oneOf(data.flatMap((all: AllOf[A]) => f(all)))
  }

  def filter(f: List[A] => Boolean): Interpretation[A] = {
    Interpretation(data.filter(f))
  }

  def distinct: Interpretation[A] = {
    Interpretation(data.map((all: AllOf[A]) => all.distinct))
  }

  def isEmpty: Boolean = data.headOption.getOrElse(Nil).isEmpty

  override def toString: String = data.map(_.mkString(" and ")).map("(" + _ + ")").mkString(" or ")

  private def combineLists[T](l1: List[List[T]], l2: List[T]): List[List[T]] = {
    if (l2 == Nil) l1
    else l2.flatMap((elem: T) => l1.map((list: List[T]) => elem :: list))
  }

  private def expandAllOf[T](a: AllOf[A])(f: A => List[T]): Interpretation[T] = {
    Interpretation(a.map(f).foldLeft(List(List[T]()))((res, elem) => combineLists[T](res, elem)))
  }
}

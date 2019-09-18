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
final case class Interpretation[A] private (data: OneOf[AllOf[A]]) {

  def add(that: Interpretation[A]) = Interpretation(data ++ that.data)

  override def toString: String = "i?" + data.map(_.mkString("(", " and ", ")")).mkString(" or ")

}

object Interpretation {

  private type OneOf[A] = Seq[A]
  private type AllOf[A] = Seq[A]

  def none[A]: Interpretation[A] = Interpretation(Seq())

  def only[A](a: A): Interpretation[A] = Interpretation(Seq(Seq(a)))

  def oneOf[A](l: A*): Interpretation[A] = Interpretation(l.map(Seq(_)))

  def allOf[A](l: A*): Interpretation[A] = Interpretation(Seq(l))

}

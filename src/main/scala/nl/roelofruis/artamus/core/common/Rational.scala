package nl.roelofruis.artamus.core.common

import scala.annotation.tailrec

/** Models a rational number that can be used to express exact fractional calculations.
 *
 * @see https://en.wikipedia.org/wiki/rational_number
 *
 * @param n The numerator
 * @param d The denominator
 */
final case class Rational private (n: Int, d: Int) extends Ordered[Rational] {
  def *(a: Int): Rational = Rational.apply(a * n, d)
  def *(that: Rational): Rational = Rational.apply(n * that.n, d * that.d)

  def /(a: Int): Rational = Rational.apply(n, d * a)
  def /(that: Rational): Rational = Rational.apply(n * that.d,d * that.n)
  def %(that: Rational): Int = {
    val res = this / that
    res.n / res.d
  }

  def -(that: Rational): Rational = {
    if (that.n == 0) this
    else if (this.n == 0) that * -1
    else Rational.apply(n * that.d - that.n * d, d * that.d)
  }

  def +(that: Rational): Rational = {
    if (that.n == 0) this
    else if (this.n == 0) that
    else Rational.apply(n * that.d + that.n * d, d * that.d)
  }

  override def compare(that: Rational): Int = {
    if (that.n == 0) this.n
    else if (this.n == 0) -that.n
    else (this - that).n
  }

  def reciprocal: Rational = Rational(d, n)

  /** Casting to double probably loses precision! */
  def toDouble: Double = {
    if (d == 0) 0D
    else n / d.toDouble
  }

  override def toString: String = {
    if (n == 0 || d == 0) "0"
    else {
      val i = n / d
      val rest = {
        if ((n - i) != 0) {
          val dispN = if (i < 0) (n - (i * d)) * -1 else n - (i * d)
          s"$dispN/$d"
        } else ""
      }
      if (i != 0) {
        if (rest.nonEmpty) s"$i $rest"
        else s"$i"
      } else s"$rest"
    }
  }

}

object Rational {

  def reciprocal(n: Int): Rational = Rational(1, n)

  def apply(n: Int): Rational = Rational(n, 1)

  def apply(n: Int, d: Int): Rational = {
    if (n == 0) new Rational(0, 0)
    else if (d < 0) apply(n * -1, d * -1)
    else {
      val g = greatestCommonDivisor(Math.abs(n), Math.abs(d))
      new Rational(n / g, d / g)
    }
  }

  @tailrec
  private def greatestCommonDivisor(x: Int, y:Int): Int = {
    if (x == 0) y
    else if (y == 0) x
    else if (x < y) greatestCommonDivisor(x, y-x)
    else greatestCommonDivisor(x-y, y)
  }

}
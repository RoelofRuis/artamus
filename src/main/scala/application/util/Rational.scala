package application.util

case class Rational private (n: Int, d: Int) extends Ordered[Rational] {
  def *(a: Int): Rational = Rational.apply(a * n, d)

  def /(a: Int): Rational = Rational.apply(n, d * a)

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
    else if (this.n == 0) that.n
    else (this - that).n
  }

  override def toString: String = {
    if (n == 0 || d == 0) "0"
    else {
      val i = n / d
      val rest = if ((n - i) != 0) s"${n - (i * d)}/$d" else ""
      if (i != 0) s"$i $rest" else s"$rest"
    }
  }

}

object Rational {

  def apply(n: Int, d: Int): Rational = {
    if (n == 0) new Rational(0, 0)
    else {
      val g = gcd(Math.abs(n), Math.abs(d))
      new Rational(n / g, d / g)
    }
  }

  private def gcd(x: Int, y:Int): Int = {
    if (x == 0) y
    else if (y == 0) x
    else if (x < y) gcd(x, y-x)
    else gcd(x-y, y)
  }

}
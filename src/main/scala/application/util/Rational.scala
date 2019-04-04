package application.util

case class Rational private (n: Int, d: Int) {
  def *(a: Int): Rational = Rational.apply(a * n, d)

  def /(a: Int): Rational = Rational.apply(n, d * a)

  override def toString: String = {
    if (n == 0 || d == 0) "0"
    else {
      val i = n / d
      val rest = if ((n - i) > 0) s" ${n - (i * d)}/$d" else ""
      if (i > 0) s"$i $rest" else s"$rest"
    }
  }
}

object Rational {

  def apply(n: Int, d: Int): Rational = {
    val g = gcd(n, d)
    new Rational(n / g, d / g)
  }

  private def gcd(x: Int, y:Int): Int = {
    if (x == 0) y
    else if (y == 0) x
    else if (x < y) gcd(x, y-x)
    else gcd(x-y, y)
  }

}
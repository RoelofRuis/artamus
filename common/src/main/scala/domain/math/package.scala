package domain

import scala.math.pow

package object math {

  implicit class IntegerMath(i: Int) {
    /** Calculate integer powers */
    def**(b: Int): Int = pow(i.toDouble, b.toDouble).intValue

    /** Whether this integer is a power of two */
    def isPowerOfTwo: Boolean = (i != 0) && ((i & (i - 1)) == 0)

    /** Get most significant bit, essentially finding `x` in `i = 2 ^ x` for powers of two*/
    def msb: Int = {
      var n = i
      n |= n >> 1
      n |= n >> 2
      n |= n >> 4
      n |= n >> 8
      n |= n >> 16
      n += 1
      n >> 1
    }
  }

}

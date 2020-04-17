package domain

import scala.math.pow

package object math {

  implicit class IntegerMath(i: Int) {
    /** Calculate integer powers */
    def**(b: Int): Int = pow(i.toDouble, b.toDouble).intValue

    /** Whether this integer is a power of two */
    def isPowerOfTwo: Boolean = (i != 0) && ((i & (i - 1)) == 0)

    /** Find the largest `x` for `i = 2 ^ x` that would fit in the given number */
    def largestPowerOfTwo: Int = (Math.log10(i.toDouble) / Math.log10(2)).toInt
  }

}

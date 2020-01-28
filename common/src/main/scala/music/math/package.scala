package music

/** Simple math helper functions */
package object math {

  def isPowerOfTwo(i: Int): Boolean = (i != 0) && ((i & (i - 1)) == 0)

}

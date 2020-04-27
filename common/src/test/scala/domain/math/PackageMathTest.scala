package domain.math

import utest._

object PackageMathTest extends TestSuite {

  val tests: Tests = Tests {
    test("accepts powers of 2") {
      assert(1.isPowerOfTwo)
      assert(2.isPowerOfTwo)
      assert(4.isPowerOfTwo)
      assert(16.isPowerOfTwo)
      assert(1024.isPowerOfTwo)
    }
    test("rejects non powers of 2") {
      assert( ! (-2).isPowerOfTwo)
      assert( ! 0.isPowerOfTwo)
      assert( ! 3.isPowerOfTwo)
      assert( ! 15.isPowerOfTwo)
      assert( ! 1234567.isPowerOfTwo)
    }
    test("squaring numbers") {
      assert(1**1 == 1)
      assert(2**1 == 2)
      assert(2**2 == 4)
      assert(3**2 == 9)
      assert(3**3 == 27)
      assert(-2**2 == 4)
      assert(-2**3 == -8)
    }
    test("finding most significant bit") {
      assert(1.largestPowerOfTwo == 0)
      assert(2.largestPowerOfTwo == 1)
      assert(3.largestPowerOfTwo == 1)
      assert(4.largestPowerOfTwo == 2)
      assert(8.largestPowerOfTwo == 3)
      assert(9.largestPowerOfTwo == 3)
      assert(15.largestPowerOfTwo == 3)
      assert(16.largestPowerOfTwo == 4)
    }
  }

}

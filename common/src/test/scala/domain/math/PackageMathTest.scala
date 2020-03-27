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
  }

}

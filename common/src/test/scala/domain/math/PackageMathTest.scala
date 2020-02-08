package domain.math

import utest._

object PackageMathTest extends TestSuite {

  val tests: Tests = Tests {
    test("accepts powers of 2") {
      assert(math.isPowerOfTwo(1))
      assert(math.isPowerOfTwo(2))
      assert(math.isPowerOfTwo(4))
      assert(math.isPowerOfTwo(16))
      assert(math.isPowerOfTwo(1024))
    }
    test("rejects non powers of 2") {
      assert( ! math.isPowerOfTwo(-2))
      assert( ! math.isPowerOfTwo(0))
      assert( ! math.isPowerOfTwo(3))
      assert( ! math.isPowerOfTwo(15))
      assert( ! math.isPowerOfTwo(1234567))
    }
  }

}

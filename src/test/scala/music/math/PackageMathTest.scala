package music.math

import utest._

object PackageMathTest extends TestSuite {

  val tests: Tests = Tests {
    test("accepts powers of 2") {
      assert(music.math.isPowerOfTwo(1))
      assert(music.math.isPowerOfTwo(2))
      assert(music.math.isPowerOfTwo(4))
      assert(music.math.isPowerOfTwo(16))
      assert(music.math.isPowerOfTwo(1024))
    }
    test("rejects non powers of 2") {
      assert( ! music.math.isPowerOfTwo(-2))
      assert( ! music.math.isPowerOfTwo(0))
      assert( ! music.math.isPowerOfTwo(3))
      assert( ! music.math.isPowerOfTwo(15))
      assert( ! music.math.isPowerOfTwo(1234567))
    }
  }

}

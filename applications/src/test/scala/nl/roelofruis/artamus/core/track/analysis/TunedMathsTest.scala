package nl.roelofruis.artamus.core.track.analysis

import nl.roelofruis.artamus.core.track.Pitched.PitchDescriptor
import nl.roelofruis.artamus.core.track.analysis.TunedMaths.TuningDefinition
import utest._

object TunedMathsTest extends TestSuite with TunedMaths {
  val tuning: TuningDefinition = new TuningDefinition {
    override val pitchClassSequence: List[Int] = List(0, 2, 4, 5, 7, 9, 11)
    override val numPitchClasses: Int = 12
  }

  val tests: Tests = Tests {
    test("correct number of steps") {
      assert(numSteps == 7)
    }
    test("correct pitch descriptors") {
      println(getAllPitchDescriptors)
      assert(getAllPitchDescriptors == List(
        PitchDescriptor(0, 11), PitchDescriptor(0, 0), PitchDescriptor(0, 1),
        PitchDescriptor(1, 1), PitchDescriptor(1, 2), PitchDescriptor(1, 3),
        PitchDescriptor(2, 3), PitchDescriptor(2, 4), PitchDescriptor(2, 5),
        PitchDescriptor(3, 4), PitchDescriptor(3, 5), PitchDescriptor(3, 6),
        PitchDescriptor(4, 6), PitchDescriptor(4, 7), PitchDescriptor(4, 8),
        PitchDescriptor(5, 8), PitchDescriptor(5, 9), PitchDescriptor(5, 10),
        PitchDescriptor(6, 10), PitchDescriptor(6, 11), PitchDescriptor(6, 0)
      ))
    }
    test("pitch descriptor addition") {
      assert(
        PitchDescriptor(0, 0) + PitchDescriptor(0, 0) == PitchDescriptor(0, 0),
        PitchDescriptor(0, 0) + PitchDescriptor(1, 1) == PitchDescriptor(1, 1),
        PitchDescriptor(0, 0) + PitchDescriptor(6, 11) == PitchDescriptor(6, 11),
        PitchDescriptor(1, 2) + PitchDescriptor(1, 2) == PitchDescriptor(2, 4),
        PitchDescriptor(2, 4) + PitchDescriptor(2, 4) == PitchDescriptor(4, 8),
        PitchDescriptor(4, 8) + PitchDescriptor(4, 8) == PitchDescriptor(1, 4),
      )
    }
    test("pitch descriptor subtraction") {
      assert(
        PitchDescriptor(0, 0) - PitchDescriptor(0, 0) == PitchDescriptor(0, 0)
      )
    }
    test("enharmonic equivalents") {
      assert(
        PitchDescriptor(0, 0).enharmonicEquivalent.contains(PitchDescriptor(6, 0)),
        PitchDescriptor(6, 0).enharmonicEquivalent.contains(PitchDescriptor(0, 0)),
        PitchDescriptor(1, 2).enharmonicEquivalent.isEmpty,
        PitchDescriptor(4, 8).enharmonicEquivalent.contains(PitchDescriptor(5, 8)),
        PitchDescriptor(5, 8).enharmonicEquivalent.contains(PitchDescriptor(4, 8)),
      )
    }
  }

}

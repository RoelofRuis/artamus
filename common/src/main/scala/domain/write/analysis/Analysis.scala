package domain.write.analysis

import domain.primitives._

object Analysis {

  trait Intervals {
    val intervals: Set[Interval]
  }

  trait Functions {
    val intervalFunctionMapping: PartialFunction[Interval, Set[Function]]
  }

  trait Chords {
    val functionChordMapping: Seq[(Set[Function], String)]
  }

}


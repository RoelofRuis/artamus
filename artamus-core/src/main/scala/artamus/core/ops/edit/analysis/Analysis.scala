package artamus.core.ops.edit.analysis

import artamus.core.model.primitives._

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


package music.analysis

import music.primitives._

import scala.collection.immutable.SortedSet

object Analysis {

  trait DefinedFunctions[A] {
    val intervalFunctionMapping: PartialFunction[Interval, Set[Function]]
  }

  trait DefinedIntervals[A] {
    val intervals: Set[Interval]
  }

  trait DefinedChords[A] {
    val functionChordMapping: Seq[(SortedSet[Function], String)]
  }

  implicit class FunctionAnalysisOps[A : DefinedFunctions](a: TuningSystem[A]) {
    def possibleFunctions(i: Interval): Set[Function] = {
      implicitly[DefinedFunctions[A]].intervalFunctionMapping.applyOrElse(i, (_: Interval) => Set[Function]())
    }
  }

  implicit class IntervalAnalysisOps[A : DefinedIntervals](a: TuningSystem[A]) {
    def possibleIntervals(pc1: PitchClass, pc2: PitchClass): Set[Interval] = {
      val diff = a.pcDiff(pc1, pc2)
      implicitly[DefinedIntervals[A]].intervals.filter(_.pc.value == diff)
    }
  }

  implicit class ChordAnalysisOps[A : DefinedChords](a: TuningSystem[A]) {
    def functionsToName(functions: SortedSet[Function]): Option[String] = {
      implicitly[DefinedChords[A]].functionChordMapping.toMap.get(functions)
    }
  }

}

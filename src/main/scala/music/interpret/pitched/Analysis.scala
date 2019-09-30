package music.interpret.pitched

import music.symbolic.pitched.{Function, Interval, PitchClass, TuningSystem}

object Analysis {

  trait DefinedFunctions[A] {
    val intervalFunctionMapping: PartialFunction[Interval, Seq[Function]]
  }

  trait DefinedIntervals[A] {
    val intervals: Seq[Interval]
  }

  trait DefinedChords[A] {
    val functionChordMapping: Seq[(Seq[Function], String)]
  }

  implicit class FunctionAnalysisOps[A : DefinedFunctions](a: TuningSystem[A]) {
    def possibleFunctions(i: Interval): Seq[Function] = {
      implicitly[DefinedFunctions[A]].intervalFunctionMapping.applyOrElse(i, (_: Interval) => Seq[Function]())
    }
  }

  implicit class IntervalAnalysisOps[A : DefinedIntervals](a: TuningSystem[A]) {
    def possibleIntervals(pc1: PitchClass, pc2: PitchClass): Seq[Interval] = {
      val diff = a.pcDiff(pc1, pc2)
      implicitly[DefinedIntervals[A]].intervals.filter(_.pc.value == diff)
    }
  }

  implicit class ChordAnalysisOps[A : DefinedChords](a: TuningSystem[A]) {
    def functionsToName(functions: Seq[Function]): Option[String] = {
      implicitly[DefinedChords[A]].functionChordMapping.toMap.get(functions.sorted)
    }
  }

}

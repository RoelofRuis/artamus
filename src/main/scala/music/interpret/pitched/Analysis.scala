package music.interpret.pitched

import music.symbolic.pitched.{Function, Interval, PitchClass, TuningSystem}

object Analysis {

  trait DefinedFunctions[A] {
    val INTERVAL_FUNCTION_MAPPING: PartialFunction[Interval, Seq[Function]]
  }

  trait DefinedIntervals[A] {
    val INTERVALS: Seq[Interval]
  }

  trait DefinedChords[A] {
    val FUNCTION_CHORD_MAPPING: Seq[(Seq[Function], String)]
  }

  implicit class FunctionAnalysisOps[A : DefinedFunctions](a: TuningSystem[A]) {
    def possibleFunctions(i: Interval): Seq[Function] = {
      implicitly[DefinedFunctions[A]].INTERVAL_FUNCTION_MAPPING.applyOrElse(i, (_: Interval) => Seq[Function]())
    }
  }

  implicit class IntervalAnalysisOps[A : DefinedIntervals](a: TuningSystem[A]) {
    def possibleIntervals(pc1: PitchClass, pc2: PitchClass): Seq[Interval] = {
      val diff = a.pcDiff(pc1, pc2)
      implicitly[DefinedIntervals[A]].INTERVALS.filter(a.interval2pc(_).value == diff)
    }
  }

  implicit class ChordAnalysisOps[A : DefinedChords](a: TuningSystem[A]) {
    def functionsToName(functions: Seq[Function]): Option[String] = {
      implicitly[DefinedChords[A]].FUNCTION_CHORD_MAPPING.toMap.get(functions.sorted)
    }
  }

}

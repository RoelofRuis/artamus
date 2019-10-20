package music.analysis

import music.analysis.Analysis.{DefinedChords, DefinedFunctions, DefinedIntervals}
import music.primitives._

object Analysis {

  trait DefinedIntervals {
    val intervals: Set[Interval]
  }

  trait DefinedFunctions {
    val intervalFunctionMapping: PartialFunction[Interval, Set[Function]]
  }

  trait DefinedChords {
    val functionChordMapping: Seq[(Set[Function], String)]
  }

}

trait Analysis {

  implicit class IntervalAnalysisOps(tuning: TuningSystem)(implicit intervals: DefinedIntervals) {
    def possibleIntervals(pc1: PitchClass, pc2: PitchClass): Set[Interval] = {
      val diff = pc1.diff(pc2)(tuning)
      intervals.intervals.filter(_.pc.value == diff)
    }
  }

  implicit class FunctionAnalysisOps(tuning: TuningSystem)(implicit functions: DefinedFunctions) {
    def possibleFunctions(i: Interval): Set[Function] = {
      functions.intervalFunctionMapping.applyOrElse(i, (_: Interval) => Set[Function]())
    }
  }

  implicit class ChordAnalysisOps(tuning: TuningSystem)(implicit chords: DefinedChords) {
    def functionsToName(functions: Set[Function]): Option[String] = {
      chords.functionChordMapping.toMap.get(functions)
    }
  }

}

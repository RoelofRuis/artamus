package music.analysis

import music.analysis.Analysis.{Chords, Functions, Intervals}
import music.primitives.{Function, Interval, PitchClass}

final case class TuningSystem(pcSeq: Seq[Int]) {
  val numSteps: Int = pcSeq.size
  val numPitchClasses: Int = pcSeq.last + 1

  def possibleIntervals(pc1: PitchClass, pc2: PitchClass)(implicit intervals: Intervals): Set[Interval] = {
    val diff = pc1.diff(pc2)(this)
    intervals.intervals.filter(_.pc.value == diff)
  }

  def possibleFunctions(i: Interval)(implicit functions: Functions): Set[Function] = {
    functions.intervalFunctionMapping.applyOrElse(i, (_: Interval) => Set[Function]())
  }

  def functionsToName(functions: Set[Function])(implicit chords: Chords): Option[String] = {
    chords.functionChordMapping.toMap.get(functions)
  }

}

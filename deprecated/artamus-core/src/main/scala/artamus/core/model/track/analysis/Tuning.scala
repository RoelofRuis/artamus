package artamus.core.model.track.analysis

import artamus.core.model.primitives.{Degree, Function, Interval, PitchClass}

final case class Tuning(
  tuningBase: TuningBase,
  degrees: Set[Degree],
  intervalFunctionMapping: PartialFunction[Interval, Set[Function]],
  intervals: Set[Interval],
  functionChordMapping: Seq[(Set[Function], String)]
) {

  def possibleIntervals(pc1: PitchClass, pc2: PitchClass): Set[Interval] = {
    val diff = pc1.diff(pc2)(tuningBase)
    intervals.filter(_.pc.value == diff)
  }

  def possibleFunctions(i: Interval): Set[Function] = {
    intervalFunctionMapping.applyOrElse(i, (_: Interval) => Set[Function]())
  }

  def functionsToName(functions: Set[Function]): Option[String] = {
    functionChordMapping.toMap.get(functions)
  }

}

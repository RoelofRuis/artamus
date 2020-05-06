package artamus.core.ops.edit.analysis

import artamus.core.model.primitives.{Chord, _}

object TwelveToneChordAnalysis {

  import TwelveToneTuning._

  def findChords(set: Seq[PitchClass]): Seq[Chord] = {
    PitchClass.listAll.flatMap{ root =>
      Interpretation.allOf(set.toSet)
        .expand(pc => tuning.possibleIntervals(root, pc))
        .expand(tuning.possibleFunctions)
        .filter(functions => tuning.functionsToName(Set(functions.toSeq: _*)).nonEmpty)
        .data.map(functions => Chord(root, Set(functions.toSeq: _*)))
    }
  }

}

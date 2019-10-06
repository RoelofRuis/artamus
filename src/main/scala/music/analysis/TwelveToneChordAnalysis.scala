package music.analysis

import music.symbolic.pitch.{Chord, PitchClass}
import server.analysis.Interpretation

object TwelveToneChordAnalysis {

  import music.analysis.Analysis._
  import TwelveToneEqualTemprament._

  def findChords(set: Seq[PitchClass]): Seq[Chord] = {
    tuning.pcs.flatMap{ root =>
      Interpretation.allOf(set)
        .expand(pc => tuning.possibleIntervals(root, pc))
        .expand(tuning.possibleFunctions)
        .filter(tuning.functionsToName(_).nonEmpty)
        .data.map(functions => Chord(root, functions))
    }
  }

}

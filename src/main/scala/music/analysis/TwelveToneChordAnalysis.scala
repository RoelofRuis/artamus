package music.analysis

import music.primitives._
import server.analysis.Interpretation

object TwelveToneChordAnalysis {

  import TwelveToneEqualTemprament._
  import music.analysis.Analysis._

  def findChords(set: Seq[PitchClass]): Seq[(ChordRoot, ChordFunctions)] = {
    tuning.pcs.flatMap{ root =>
      Interpretation.allOf(set)
        .expand(pc => tuning.possibleIntervals(root, pc))
        .expand(tuning.possibleFunctions)
        .filter(tuning.functionsToName(_).nonEmpty)
        .data.map(functions => (ChordRoot(root), ChordFunctions(functions)))
    }
  }

}

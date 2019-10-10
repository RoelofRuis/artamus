package music.analysis

import music.collection.SymbolProperties
import music.primitives._
import music.symbols.Chord
import server.analysis.Interpretation

object TwelveToneChordAnalysis {

  import TwelveToneEqualTemprament._
  import music.analysis.Analysis._

  def findChords(set: Seq[PitchClass]): Seq[SymbolProperties[Chord.type]] = {
    tuning.pcs.flatMap{ root =>
      Interpretation.allOf(set)
        .expand(pc => tuning.possibleIntervals(root, pc))
        .expand(tuning.possibleFunctions)
        .filter(tuning.functionsToName(_).nonEmpty)
        .data.map(functions => Chord(root, functions))
    }
  }

}

package music.analysis

import music.primitives._
import music.symbols.Chord

import scala.collection.immutable.SortedSet

object TwelveToneChordAnalysis {

  import TwelveToneEqualTemprament._
  import music.analysis.Analysis._

  def findChords(set: Seq[PitchClass]): Seq[Chord] = {
    tuning.pcs.flatMap{ root =>
      Interpretation.allOf(set.toSet)
        .expand(pc => tuning.possibleIntervals(root, pc))
        .expand(tuning.possibleFunctions)
        .filter(functions => tuning.functionsToName(SortedSet(functions.toSeq: _*)).nonEmpty)
        .data.map(functions => Chord(root, SortedSet(functions.toSeq: _*)))
    }
  }

}

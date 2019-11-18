package music.analysis

import music.primitives._
import music.domain.track.symbol.Chord

import scala.collection.immutable.SortedSet

object TwelveToneChordAnalysis {

  import TwelveToneTuning._

  def findChords(set: Seq[PitchClass]): Seq[Chord] = {
    PitchClass.listAll.flatMap{ root =>
      Interpretation.allOf(set.toSet)
        .expand(pc => tuning.possibleIntervals(root, pc))
        .expand(tuning.possibleFunctions)
        .filter(functions => tuning.functionsToName(SortedSet(functions.toSeq: _*)).nonEmpty)
        .data.map(functions => Chord(root, SortedSet(functions.toSeq: _*)))
    }
  }

}

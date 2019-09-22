package music.interpret

import music.symbolic.pitched.{Chord, PitchClass}

object ChordFinder {

  import music.symbolic.pitched.TwelveToneEqualTemprament._
  import music.symbolic.pitched.Analysis._

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

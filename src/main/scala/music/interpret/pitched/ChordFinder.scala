package music.interpret.pitched

import music.interpret.Interpretation
import music.symbolic.pitch.{Chord, PitchClass}

object ChordFinder {

  import music.interpret.pitched.Analysis._
  import music.interpret.pitched.TwelveToneEqualTemprament._

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

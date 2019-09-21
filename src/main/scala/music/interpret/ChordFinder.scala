package music.interpret

import music.symbolic.Pitched.{Chord, PitchClass}
import music.symbolic.tuning.TwelveToneEqualTemprament

object ChordFinder {

  val tuning = TwelveToneEqualTemprament

  sealed trait ChordType
  case object Major extends ChordType
  case object Minor extends ChordType

  def findChords(pcs: Seq[PitchClass]): Seq[Chord] = {
    tuning.pitchClasses.flatMap{ root =>
      Interpretation.allOf(pcs)
        .expand(pc => tuning.possibleIntervals(root, pc))
        .expand(tuning.functions)
        .filter(tuning.chordMap(_).nonEmpty)
        .data.map(functions => Chord(root, functions))
    }
  }

}

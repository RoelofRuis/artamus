package music.interpret

import music.symbolic.Pitched.{Chord, PitchClass}

object ChordFinder {

  sealed trait ChordType
  case object Major extends ChordType
  case object Minor extends ChordType

  def findChords(l: Seq[PitchClass]): Seq[Chord] = ???

//    ROOTS.flatMap { root =>
//      pitchInterpretation
//        .expand(pc => pitchClassAsInterval(root, pc).toList) // Intervals
//        .expand(mv => intervalToFunctions(Interval_Old(mv)).toList) // Functions
//        .mapAll(findChord)
//        .data.flatten.map(tpe => Chord_Old(root, tpe))
//    }

//  private def intervalToFunctions(i: Interval_Old): Seq[IntervalFunction_Old] = {
//    i match {
//      case Intervals.PRIME => Seq(IntervalFunctions.ROOT)
//      case Intervals.FLAT_TWO => Seq(IntervalFunctions.FLAT_NINE)
//      case Intervals.TWO => Seq(IntervalFunctions.TWO, IntervalFunctions.NINE)
//      case Intervals.FLAT_THREE => Seq(IntervalFunctions.FLAT_THREE, IntervalFunctions.FLAT_TEN)
//      case Intervals.THREE => Seq(IntervalFunctions.THREE)
//      case Intervals.FOUR => Seq(IntervalFunctions.FOUR, IntervalFunctions.ELEVEN)
//      case Intervals.SHARP_FOUR => Seq(IntervalFunctions.SHARP_ELEVEN)
//      case Intervals.FLAT_FIVE => Seq(IntervalFunctions.FLAT_FIVE)
//      case Intervals.FIVE => Seq(IntervalFunctions.FIVE)
//      case Intervals.SHARP_FIVE => Seq(IntervalFunctions.SHARP_FIVE)
//      case Intervals.FLAT_SIX => Seq(IntervalFunctions.FLAT_THIRTEEN)
//      case Intervals.SIX => Seq(IntervalFunctions.SIX, IntervalFunctions.THIRTEEN)
//      case Intervals.DIM_SEVEN => Seq(IntervalFunctions.DIM_SEVEN)
//      case Intervals.FLAT_SEVEN => Seq(IntervalFunctions.FLAT_SEVEN)
//      case Intervals.SEVEN => Seq(IntervalFunctions.SEVEN)
//      case _ => Seq()
//    }
//  }

}

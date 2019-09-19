package music.interpret

import music.symbolic._
import music.symbolic.const.{IntervalFunctions, Intervals}
import music.symbolic.tuning.{Tuning, TwelveToneEqualTemprament}

object ChordFinder {

  val tuning: Tuning = TwelveToneEqualTemprament

  sealed trait ChordType
  case object Major extends ChordType

  def findChords(l: Seq[PitchClass]): Seq[Chord] = {
    val pitchInterpretation = Interpretation.allOf(l.toList).distinct

    ROOTS.flatMap { root =>
      pitchInterpretation
        .expand(pc => pitchClassAsInterval(root, pc).toList) // Intervals
        .expand(mv => intervalToFunctions(Interval(mv)).toList) // Functions
        .mapAll(findChord)
        .data.flatten.map(tpe => Chord(root, tpe))
    }
  }

  private def findChord(l: List[IntervalFunction]): Option[ChordType] = {
    l.sorted match {
      case IntervalFunctions.ROOT :: IntervalFunctions.THREE :: IntervalFunctions.FIVE :: Nil => Some(Major)
      case _ => None
    }
  }

  private def pitchClassAsInterval(root: MusicVector, pc: PitchClass): Seq[MusicVector] = {
    val negAccVal = - root.acc.value
    Range.inclusive(negAccVal - 1, negAccVal + 2)
      .flatMap { mod =>
        tuning.pitchClassToStep(PitchClass(pc.value + mod))
          .map(step => MusicVector(step, Accidental(-mod)))
          .map(tuning.subtract(_, root))
          .map(tuning.rectify)
          .filter(Intervals.WITHIN_OCTAVE.map(_.musicVector).contains)
      }
  }

  private val ROOTS: Seq[MusicVector] = Seq(
    tuning.vector(0, 0),
    tuning.vector(0, 1),
    tuning.vector(1, -1),
    tuning.vector(1, 0),
    tuning.vector(1, 1),
    tuning.vector(2, -1),
    tuning.vector(2, 0),
    tuning.vector(3, 0),
    tuning.vector(3, 1),
    tuning.vector(4, -1),
    tuning.vector(4, 0),
    tuning.vector(4, 1),
    tuning.vector(5, -1),
    tuning.vector(5, 0),
    tuning.vector(5, 1),
    tuning.vector(6, -1),
    tuning.vector(6, 0)
  )

  private def intervalToFunctions(i: Interval): Seq[IntervalFunction] = {
    i match {
      case Intervals.PRIME => Seq(IntervalFunctions.ROOT)
      case Intervals.FLAT_TWO => Seq(IntervalFunctions.FLAT_NINE)
      case Intervals.TWO => Seq(IntervalFunctions.TWO, IntervalFunctions.NINE)
      case Intervals.FLAT_THREE => Seq(IntervalFunctions.FLAT_THREE, IntervalFunctions.FLAT_TEN)
      case Intervals.THREE => Seq(IntervalFunctions.THREE)
      case Intervals.FOUR => Seq(IntervalFunctions.FOUR, IntervalFunctions.ELEVEN)
      case Intervals.SHARP_FOUR => Seq(IntervalFunctions.SHARP_ELEVEN)
      case Intervals.FLAT_FIVE => Seq(IntervalFunctions.FLAT_FIVE)
      case Intervals.FIVE => Seq(IntervalFunctions.FIVE)
      case Intervals.SHARP_FIVE => Seq(IntervalFunctions.SHARP_FIVE)
      case Intervals.FLAT_SIX => Seq(IntervalFunctions.FLAT_THIRTEEN)
      case Intervals.SIX => Seq(IntervalFunctions.SIX, IntervalFunctions.THIRTEEN)
      case Intervals.DIM_SEVEN => Seq(IntervalFunctions.DIM_SEVEN)
      case Intervals.FLAT_SEVEN => Seq(IntervalFunctions.FLAT_SEVEN)
      case Intervals.SEVEN => Seq(IntervalFunctions.SEVEN)
      case _ => Seq()
    }
  }

}

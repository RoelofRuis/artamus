package nl.roelofruis.artamus.application

import fastparse.Parsed.{Failure => FastparseFailure, Success => FastparseSuccess}
import fastparse.SingleLineWhitespace._
import fastparse._
import nl.roelofruis.artamus.application.Model.{ParseError, ParseResult, PitchedObjects}
import nl.roelofruis.artamus.core.common.Maths._
import nl.roelofruis.artamus.core.track.Pitched._
import nl.roelofruis.artamus.core.track.Temporal.{BeatGroup, Metre, PulseGroup}
import nl.roelofruis.artamus.core.track.algorithms.PitchedMaths.TuningDefinition

import scala.util.{Failure, Success}

object ObjectParsers {

  val NOTES: Seq[String] = Seq("C", "D", "E", "F", "G", "A", "B")
  val DEGREES: Seq[String] = Seq("I", "II", "III", "IV", "V", "VI", "VII")
  val INTERVALS: Seq[String] = Seq("1", "2", "3", "4", "5", "6", "7")
  val FLAT: String = "b"
  val SHARP: String = "#"

  def oneOf[_: P](options: Seq[String]): P[String] = options.sortBy(-_.length).foldLeft(P[Unit](Fail))(_ | _).!
  def fromMap[_ : P, A](map: Map[String, A]): P[A] = oneOf(map.keys.toSeq).map(map(_))
  def exists[_ : P](s: String): P[Boolean] = P(s.?.!.map(_.length == 1))

  def doParse[T](input: String, parser: P[_] => P[T], verbose: Boolean = false): ParseResult[T] = {
    fastparse.parse(input, parser(_), verbose) match {
      case FastparseSuccess(t, _) => Success(t)
      case f: FastparseFailure => Failure(ParseError(if (! verbose) f.msg else f.longMsg))
    }
  }

  def step[_ : P](options: Seq[String]): P[Int] = P(oneOf(options).map{options.indexOf(_)})

  def flats[_ : P]: P[Int] = P(FLAT.rep(1).!.map(- _.length))
  def sharps[_ : P]: P[Int] = P(SHARP.rep(1).!.map(_.length))
  def accidentals[_ : P]: P[Int] = P(flats | sharps | "".!.map(_ => 0))

  implicit class FromTuningDefinition(tuning: TuningDefinition) {
    def pitchDescriptor[_ : P]: P[PitchDescriptor] = P((step(NOTES) ~ accidentals).map { case (step, accidentals) =>
      PitchDescriptor(step, tuning.pitchClassSequence(step) + accidentals)
    })

    def degreeDescriptor[_ : P]: P[PitchDescriptor] = P((accidentals ~ step(DEGREES)).map { case (accidentals, step) =>
      PitchDescriptor(step, tuning.pitchClassSequence(step) + accidentals)
    })

    def interval[_ : P]: P[PitchDescriptor] = P((accidentals ~ step(INTERVALS)).map { case (accidentals, step) =>
      PitchDescriptor(step, tuning.pitchClassSequence(step) + accidentals)
    })

    def metre[_ : P]: P[Metre] = P((pulseGroup ~ ("+" ~ pulseGroup).rep).map { case (pulseGroup, pulseGroupList) =>
      Metre(pulseGroup +: pulseGroupList)
    })

    def pulseGroup[_ : P]: P[PulseGroup] = P((CharIn("123").!.map {
      case "1" => BeatGroup.Single
      case "2" => BeatGroup.Double
      case "3" => BeatGroup.Triple
    } ~ "/" ~ CharIn("0-9").rep(1).!.flatMap { s =>
      val intVal = s.toInt
      if (intVal.isPowerOfTwo) Pass(intVal.largestPowerOfTwo)
      else Fail
    }).map { case (pulses, base) => PulseGroup(base, pulses) })
  }

  implicit class FromPitchedObjects(tuning: TuningDefinition with PitchedObjects) {
    def scale[_ : P]: P[Scale] = fromMap(tuning.scaleMap)

    def key[_ : P]: P[Key] = P((tuning.pitchDescriptor ~ scale).map { case (pd, scale) => Key(pd, scale) })

    def quality[_ : P]: P[Quality] = fromMap(tuning.qualityMap)

    def qualityGroup[_ : P]: P[QualityGroup] = fromMap(tuning.qualityGroupMap)

    def chord[_ : P]: P[Chord] = P((tuning.pitchDescriptor ~ quality).map { case (pd, quality) => Chord(pd, quality) })

    def degree[_ : P]: P[Degree] = P(
      (tuning.degreeDescriptor ~~ qualityGroup ~~ exists("T") ~~ ("/" ~ tuning.degreeDescriptor).?).map{
        case (root, quality, isTritoneSub, relativeTo) => Degree(root, quality, relativeTo, isTritoneSub)
      })
  }

}

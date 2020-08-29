package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.core.Pitched.{Quality, Scale}
import nl.roelofruis.artamus.core.Temporal.Metre
import nl.roelofruis.artamus.core.analysis.TuningMaths.TuningDefinition

import scala.util.Try

object Model {

  type ParseResult[A] = Try[A]

  final case class ParseError(message: String, input: String) extends Exception

  trait PitchedPrimitives {
    val pitchClassSequence: List[Int]
    val textDegrees: List[String]
    val textNotes: List[String]
    val textIntervals: List[String]
    val textSharp: String
    val textFlat: String
  }

  trait PitchedObjects {
    val scaleMap: Map[String, Scale]
    val qualityMap: Map[String, Quality]
  }

  trait Temporal {
    val textBarLine: String
    val textBeatIndication: String
    val defaultMetre: Metre
  }

  final case class Settings(
    pitchClassSequence: List[Int],
    numPitchClasses: Int,
    textNotes: List[String],
    textIntervals: List[String],
    textSharp: String,
    textFlat: String,
    textBarLine: String,
    textBeatIndication: String,
    textDegrees: List[String],
    scaleMap: Map[String, Scale],
    qualityMap: Map[String, Quality],
    defaultMetre: Metre,
  ) extends PitchedPrimitives with PitchedObjects with Temporal with TuningDefinition

}

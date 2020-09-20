package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.core.track.Pitched.{Key, Quality, Scale}
import nl.roelofruis.artamus.core.track.Temporal.Metre
import nl.roelofruis.artamus.core.track.analysis.TunedMaths.TuningDefinition

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

  trait TemporalSettings {
    val textBarLine: String
    val textRepeatMark: String
  }

  trait Defaults {
    val defaultMetre: Metre
    val defaultKey: Key
  }

  final case class Settings(
    pitchClassSequence: List[Int],
    numPitchClasses: Int,
    textNotes: List[String],
    textIntervals: List[String],
    textSharp: String,
    textFlat: String,
    textBarLine: String,
    textRepeatMark: String,
    textDegrees: List[String],
    scaleMap: Map[String, Scale],
    qualityMap: Map[String, Quality],
    defaultMetre: Metre,
    defaultKey: Key,
  ) extends PitchedPrimitives with PitchedObjects with TemporalSettings with TuningDefinition with Defaults

}

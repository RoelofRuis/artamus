package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.core.track.Pitched.{Key, Quality, QualityGroup, Scale}
import nl.roelofruis.artamus.core.track.Temporal.Metre
import nl.roelofruis.artamus.core.track.algorithms.PitchedMaths.TuningDefinition

import scala.util.Try

object Model {

  type ParseResult[A] = Try[A]

  final case class ParseError(message: String) extends Exception

  trait PitchedObjects {
    val scaleMap: Map[String, Scale]
    val qualityMap: Map[String, Quality]
    val qualityGroupMap: Map[String, QualityGroup]
  }

  trait Defaults {
    val defaultMetre: Metre
    val defaultKey: Key
  }

  final case class Settings(
    pitchClassSequence: List[Int],
    numPitchClasses: Int,
    scaleMap: Map[String, Scale],
    qualityMap: Map[String, Quality],
    qualityGroupMap: Map[String, QualityGroup],
    defaultMetre: Metre,
    defaultKey: Key,
  ) extends PitchedObjects with TuningDefinition with Defaults

}

package nl.roelofruis.artamus.tuning

import nl.roelofruis.artamus.core.Model.{Quality, Scale}
import nl.roelofruis.artamus.parsing.Model.{PitchedObjects, PitchedPrimitives, TemporalPrimitives}

object Model {

  final case class Tuning(
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
  ) extends PitchedPrimitives with PitchedObjects with TemporalPrimitives

}
package nl.roelofruis.artamus.settings

import nl.roelofruis.artamus.core.Pitched.{Quality, Scale}
import nl.roelofruis.artamus.core.Temporal.Metre
import nl.roelofruis.artamus.parsing.Model.{PitchedObjects, PitchedPrimitives, TemporalPrimitives}

object Model {

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
  ) extends PitchedPrimitives with PitchedObjects with TemporalPrimitives

}

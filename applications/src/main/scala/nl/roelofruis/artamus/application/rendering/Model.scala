package nl.roelofruis.artamus.application.rendering

import nl.roelofruis.artamus.core.track.Pitched.Quality
import nl.roelofruis.artamus.core.track.analysis.TunedMaths.TuningDefinition

object Model {

  final case class LilypondSettings(
    pngResolution: Int,
    lilypondVersion: String,
    paperSize: String,
    pitchClassSequence: List[Int],
    numPitchClasses: Int,
    stepNames: List[String],
    flatSpelling: String,
    sharpSpelling: String,
    dotSpelling: String,
    qualitySpelling: Map[Quality, String]
  ) extends TuningDefinition

}

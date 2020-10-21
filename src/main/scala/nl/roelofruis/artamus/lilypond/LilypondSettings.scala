package nl.roelofruis.artamus.lilypond

import nl.roelofruis.artamus.core.track.Pitched.{Quality, QualityGroup, Scale}
import nl.roelofruis.artamus.core.track.algorithms.PitchedMaths.TuningDefinition

final case class LilypondSettings(
  pngResolution: Int,
  lilypondVersion: String,
  paperSize: String,
  pitchClassSequence: List[Int],
  numPitchClasses: Int,
  qualitySpelling: Map[Quality, String],
  qualityGroupSpelling: Map[QualityGroup, String],
  scaleSpelling: Map[Scale, String],
  scaleSymbolSpelling: Map[Scale, String],
  quarterTempo: Int
) extends TuningDefinition
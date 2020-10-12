package nl.roelofruis.artamus.core.track.algorithms.reharm

import nl.roelofruis.artamus.core.track.Layer.RomanNumeralSeq
import nl.roelofruis.artamus.core.track.algorithms.PitchedMaths
import nl.roelofruis.artamus.core.track.algorithms.PitchedMaths.TuningDefinition

case class Reharmonizer(settings: TuningDefinition) extends PitchedMaths {

  def reharmonize(rnaTrack: RomanNumeralSeq): RomanNumeralSeq = ???

}

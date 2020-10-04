package nl.roelofruis.artamus.core.track.algorithms.reharm

import nl.roelofruis.artamus.core.track.Layer.RomanNumeralSeq
import nl.roelofruis.artamus.core.track.algorithms.TunedMaths
import nl.roelofruis.artamus.core.track.algorithms.TunedMaths.TuningDefinition

case class Reharmonizer(settings: TuningDefinition) extends TunedMaths {

  def reharmonize(rnaTrack: RomanNumeralSeq): RomanNumeralSeq = ???

}

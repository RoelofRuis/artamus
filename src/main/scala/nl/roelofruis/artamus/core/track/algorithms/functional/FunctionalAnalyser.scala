package nl.roelofruis.artamus.core.track.algorithms.functional

import nl.roelofruis.artamus.application.Model.Settings
import nl.roelofruis.artamus.core.track.Layer.ChordSeq
import nl.roelofruis.artamus.core.track.algorithms.TunedMaths
import nl.roelofruis.artamus.core.track.algorithms.functional.Model.FunctionalAnalysisRules

case class FunctionalAnalyser(settings: Settings, rules: FunctionalAnalysisRules) extends TunedMaths {

  def analyse(chordTrack: ChordSeq): Unit = {
    chordTrack.foreach(println)
  }

}

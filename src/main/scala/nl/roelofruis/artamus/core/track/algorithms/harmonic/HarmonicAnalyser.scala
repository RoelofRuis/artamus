package nl.roelofruis.artamus.core.track.algorithms.harmonic

import nl.roelofruis.artamus.core.track.Layer.{ChordSeq, NoteSeq}
import nl.roelofruis.artamus.core.track.algorithms.TunedMaths
import nl.roelofruis.artamus.core.track.algorithms.TunedMaths.TuningDefinition

case class HarmonicAnalyser(settings: TuningDefinition) extends TunedMaths {

  def analyseHarmony(notes: NoteSeq): ChordSeq = ???

}

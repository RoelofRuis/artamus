package nl.roelofruis.artamus.core.track.algorithms.harmonic

import nl.roelofruis.artamus.application.Model.Settings
import nl.roelofruis.artamus.core.track.Layer.{ChordSeq, NoteSeq}
import nl.roelofruis.artamus.core.track.algorithms.TunedMaths

case class HarmonicAnalyser(settings: Settings) extends TunedMaths {

  def analyseHarmony(notes: NoteSeq): ChordSeq = ???

}

package nl.roelofruis.artamus.core.track.algorithms.harmonic

import nl.roelofruis.artamus.core.track.Track.{ChordTimeline, NoteTimeline}
import nl.roelofruis.artamus.core.track.algorithms.PitchedMaths
import nl.roelofruis.artamus.core.track.algorithms.PitchedMaths.TuningDefinition

case class HarmonicAnalyser(settings: TuningDefinition) extends PitchedMaths {

  def analyseHarmony(notes: NoteTimeline): ChordTimeline = ???

}

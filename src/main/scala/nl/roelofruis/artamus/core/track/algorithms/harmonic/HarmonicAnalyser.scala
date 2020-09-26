package nl.roelofruis.artamus.core.track.algorithms.harmonic

import nl.roelofruis.artamus.application.Model.Settings
import nl.roelofruis.artamus.core.track.Layer.{ChordTrack, NoteTrack}
import nl.roelofruis.artamus.core.track.algorithms.TunedMaths

case class HarmonicAnalyser(settings: Settings) extends TunedMaths {

  def analyseHarmony(notes: NoteTrack): ChordTrack = ???

}

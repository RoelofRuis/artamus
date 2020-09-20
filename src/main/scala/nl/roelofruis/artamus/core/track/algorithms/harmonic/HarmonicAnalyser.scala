package nl.roelofruis.artamus.core.track.algorithms.harmonic

import nl.roelofruis.artamus.application.Model.Settings
import nl.roelofruis.artamus.core.track.Pitched._
import nl.roelofruis.artamus.core.track.algorithms.TunedMaths

case class HarmonicAnalyser(settings: Settings) extends TunedMaths {

  def analyiseHarmony(notes: NoteTrack): ChordTrack = ???

}

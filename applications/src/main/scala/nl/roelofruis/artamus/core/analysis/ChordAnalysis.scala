package nl.roelofruis.artamus.core.analysis

import nl.roelofruis.artamus.core.Pitched.{Chord, Degree, PitchDescriptor}
import nl.roelofruis.artamus.settings.Model.Settings

case class ChordAnalysis(tuning: Settings) extends TuningMaths {

  def nameChords(degrees: Seq[Degree], root: PitchDescriptor): Seq[Chord] = {
    degrees.map { degree =>
      val chordPitch = degree.root + root
      Chord(chordPitch, degree.quality)
    }
  }

}

package nl.roelofruis.artamus.core.track.analysis

import nl.roelofruis.artamus.core.track.Pitched.{Chord, Degree, PitchDescriptor}
import nl.roelofruis.artamus.core.track.analysis.TunedMaths.TuningDefinition

case class ChordAnalysis(tuning: TuningDefinition) extends TunedMaths {

  def nameChords(degrees: Seq[Degree], root: PitchDescriptor): Seq[Chord] = {
    degrees.map { degree =>
      val chordPitch = degree.root + root
      Chord(chordPitch, degree.quality)
    }
  }

}

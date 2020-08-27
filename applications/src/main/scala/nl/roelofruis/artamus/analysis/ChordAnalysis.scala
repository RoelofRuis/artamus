package nl.roelofruis.artamus.analysis

import nl.roelofruis.artamus.core.Model.{Chord, Degree, PitchDescriptor}
import nl.roelofruis.artamus.tuning.Model.Tuning

case class ChordAnalysis(tuning: Tuning) extends TuningMaths {

  def nameChords(degrees: Seq[Degree], root: PitchDescriptor): Seq[Chord] = {
    degrees.map { degree =>
      val chordPitch = degree.root + root
      Chord(chordPitch, degree.quality)
    }
  }

}

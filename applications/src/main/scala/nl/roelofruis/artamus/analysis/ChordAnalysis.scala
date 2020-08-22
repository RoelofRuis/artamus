package nl.roelofruis.artamus.analysis

import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model.{Chord, Degree, PitchDescriptor}

case class ChordAnalysis(tuning: TextTuning) extends TuningMaths {

  def nameChords(degrees: Seq[Degree], root: PitchDescriptor): Seq[Chord] = {
    degrees.map { degree =>
      val chordPitch = degree.root + root
      Chord(chordPitch, degree.quality)
    }
  }

}

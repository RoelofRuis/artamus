package nl.roelofruis.artamus.analysis

import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model.{Chord, Degree, Key, PitchDescriptor}

case class RNA(tuning: TextTuning) extends TuningMaths {

  final case class DegreeHypothesis(
    chord: Chord,
    options: Seq[DegreeInKey] = Seq()
  ) {
    def add(hypothesis: DegreeInKey): DegreeHypothesis = copy(
      options = options :+ hypothesis
    )
  }

  final case class DegreeInKey(
    degreePitch: PitchDescriptor,
    key: Key,
  )

  def nameDegrees(chords: Seq[Chord], baseKey: Key): Seq[Degree] = {
    val hypotheses = chords.map { DegreeHypothesis(_) }

    val chordsInKey = hypotheses
      .map { hypothesis =>
        // Find chords that are part of the key
        val chord = hypothesis.chord
        if (baseKey.contains(chord)) hypothesis.add(DegreeInKey(chord.root - baseKey.root, baseKey))
        else hypothesis
      }

    chordsInKey.foreach { h =>
      println(h)
    }

    chords.map { chord =>
      val degreePitch = chord.root - baseKey.root

      Degree(degreePitch, chord.quality)
    }
  }

}

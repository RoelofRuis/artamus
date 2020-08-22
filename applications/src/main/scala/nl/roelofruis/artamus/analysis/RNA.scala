package nl.roelofruis.artamus.analysis

import nl.roelofruis.artamus.degree.Model._

case class RNA(tuning: Tuning) extends TuningMaths {

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

  import nl.roelofruis.artamus.tuning.Printer._

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
      val chord = tuning.printChord(h.chord)
      val options = h.options
        .map { hyp => tuning.printDegreeDescriptor(hyp.degreePitch) + " in " + tuning.printKey(hyp.key) }
        .mkString(" | ")

      println(s"$chord = $options")
    }

    chords.map { chord =>
      val degreePitch = chord.root - baseKey.root

      Degree(degreePitch, chord.quality)
    }
  }

}

package nl.roelofruis.artamus.analysis

import nl.roelofruis.artamus.degree.Model._

case class RNA(tuning: Tuning) extends TuningMaths {

  final case class DegreeHypothesis(
    chord: Chord,
    options: Seq[DegreeInKey] = Seq()
  )

  final case class DegreeInKey(
    degreePitch: PitchDescriptor,
    key: Key,
  )

  import nl.roelofruis.artamus.tuning.Printer._

  def nameDegrees(chords: Seq[Chord], baseKey: Key): Seq[Degree] = {
    val keys = baseKey.scale.asPitchDescriptors.map { descriptor =>
      Key(baseKey.root + descriptor, baseKey.scale)
    }

    val chordsInKey = chords.map { chord =>
        DegreeHypothesis(
          chord,
          keys.flatMap { key => if (key.contains(chord)) Some(DegreeInKey(chord.root - key.root, key)) else None }
        )
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

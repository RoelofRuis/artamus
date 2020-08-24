package nl.roelofruis.artamus.analysis.rna

import nl.roelofruis.artamus.analysis.TuningMaths
import nl.roelofruis.artamus.analysis.rna.Model.RNARules
import nl.roelofruis.artamus.analysis.rna.RNA.{Hypothesis, State}
import nl.roelofruis.artamus.degree.Model._

object RNA {

  final case class Hypothesis(
    chord: Chord,
    options: Seq[State] = Seq()
  )

  final case class State(
    degreePitch: PitchDescriptor,
    keyInterval: PitchDescriptor,
    key: Key
  )

}

case class RNA(tuning: Tuning, rules: RNARules) extends TuningMaths {

  import nl.roelofruis.artamus.tuning.Printer._ // TODO: remove

  def nameDegrees(chords: Seq[Chord], root: PitchDescriptor): Seq[Degree] = {
    val keys = allPitchDescriptors.flatMap { descriptor =>
      tuning.scaleMap.values.map { Key(descriptor + root, _) }
    }

    val chordsInKey = chords.map { chord =>
      val options = keys.flatMap { key =>
        if (key.contains(chord)) Some(
          State(
            chord.root - key.root,
            key.root - root,
            key
          ))
        else None
      }
      Hypothesis(chord, options)
    }

    chordsInKey.foreach { h =>
      val chord = tuning.printChord(h.chord)
      val options = h.options
        .map { hyp =>
          s"${tuning.printDegreeDescriptor(hyp.degreePitch)} in ${tuning.printKey(hyp.key)} (${tuning.printIntervalDescriptor(hyp.keyInterval)})"
        }.mkString(" | ")
      println(s"$chord = $options")
    }


    chords.map { chord =>
      val degreePitch = chord.root - root

      Degree(degreePitch, chord.quality)
    }
  }

}

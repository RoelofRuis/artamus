package nl.roelofruis.artamus.analysis

import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model.{Chord, Degree, Key, PitchDescriptor}

case class RNA(tuning: TextTuning) extends TuningMaths {

  sealed trait DegreeHypothesis

  final case object UnknownDegree extends DegreeHypothesis
  final case class DegreeInKey(
    degreePitch: PitchDescriptor,
    key: Key,
  ) extends DegreeHypothesis

  def nameDegrees(chords: Seq[Chord], baseKey: Key): Seq[Degree] = {
    import nl.roelofruis.artamus.degree.Write._ // TODO: remove this

    // Find chords that are part of the key
    val chordsInKey = chords.map { chord =>
      if (baseKey.contains(chord)) DegreeInKey(chord.root - baseKey.root, baseKey)
      else UnknownDegree
    }

    chordsInKey.map {
      case UnknownDegree => "?"
      case DegreeInKey(pitch, _) => tuning.printDegreeDescriptor(pitch)
    }.foreach(println)

    chords.map { chord =>
      val degreePitch = chord.root - baseKey.root

      Degree(degreePitch, chord.quality)
    }
  }

}

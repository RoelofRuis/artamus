package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model.{Chord, Quality, Degree, ExpansionRule, Key, PitchDescriptor}

object Harmony {

  implicit class DegreeExpansion(rules: List[ExpansionRule]) {
    import nl.roelofruis.artamus.util.Ops._

    def expandByRandomRule(degrees: List[Degree]): List[Degree] = {
      if (degrees.isEmpty) List()
      else {
        val (elem, index) = degrees.getRandomElementIndex.get
        rules.filter(_.base == elem).getRandomElement match {
          case None => degrees
          case Some(rule) => degrees.patch(index, rule.expansion, 1)
        }
      }
    }
  }

  implicit class TuningOps(tuning: TextTuning) {

    implicit class PitchDescriptorOps(descr: PitchDescriptor) {
      def +(that: PitchDescriptor): PitchDescriptor = {
        ??? // TODO: implement, see existing pitch spelling?
      }
    }

    def numSteps: Int = tuning.pitchClassSequence.size

    def nameChords(degrees: List[Degree], key: Key): List[Chord] = {
      degrees.map { degree =>
        val chordPitch = degree.pitch + key.root
        Chord(chordPitch, Quality("major"))
      }
    }

  }

}

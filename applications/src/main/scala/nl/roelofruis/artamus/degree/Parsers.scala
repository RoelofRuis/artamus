package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model._

import scala.reflect.ClassTag

object Parsers {

  implicit class TuningParseOps(tuning: TextTuning) {
    def parsePitchDescriptor: String => PitchDescriptor = input => {
      val step = tuning.noteNames.indexOf(input.replace(tuning.textSharp, "").replace(tuning.textFlat, ""))
      val pc = tuning.pitchClassSequence(step)
      val sharps = input.count(_ == tuning.textSharp.head)
      val flats = input.count(_ == tuning.textFlat.head)

      PitchDescriptor(step, pc + (sharps - flats))
    }

    def parseDegree: String => Degree = input => {
      val step = tuning.degreeNames.indexOf(input)
      val pitchClass = tuning.pitchClassSequence(step)
      Degree(PitchDescriptor(step, pitchClass), Quality("major"))
    }

    def parseArray[A : ClassTag](input: String, extractor: String => A): Array[A] = {
      input.split(' ').map(s => extractor(s))
    }
  }

}

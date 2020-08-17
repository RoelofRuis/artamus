package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model._

object Parsers {

  implicit class TuningParseOps(tuning: TextTuning) {
    def parsePitchDescriptor(input: String): PitchDescriptor = {
      val index = tuning.noteNames.indexOf(input.replace(tuning.textSharp, "").replace(tuning.textFlat, ""))
      val pc = tuning.pitchClassSequence(index)
      val sharps = input.count(_ == tuning.textSharp.head)
      val flats = input.count(_ == tuning.textFlat.head)

      PitchDescriptor(index, pc + (sharps - flats))
    }

    def parseDegrees(input: String): List[Degree] = {
      input
        .split(' ')
        .flatMap { s => tuning.degrees.find(_.text == s) }
        .map { d => Degree(PitchDescriptor(d.step, d.pitchClass), Quality("major")) }
        .toList
    }
  }

}

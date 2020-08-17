package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model._

object Parsers {

  implicit class TuningParseOps(tuning: TextTuning) {
    def parseKey(input: String): Key = {
      val parts = input.split(' ')
      val index = tuning.noteNames.indexOf(parts(0).replace(tuning.textSharp, "").replace(tuning.textFlat, ""))
      val pc = tuning.pitchClassSequence(index)
      val sharps = parts(0).count(_ == tuning.textSharp.head)
      val flats = parts(0).count(_ == tuning.textFlat.head)
      val scale = tuning.scales.find(_.name == parts(1)).get

      Key(
        PitchDescriptor(index, pc + (sharps - flats)),
        Scale(scale.pitchClassSequence)
      )
    }

    def parseDegrees(input: String): List[Degree] = {
      input
        .split(' ')
        .flatMap { s => tuning.degrees.find(_.text == s) }
        .map { d => Degree(PitchDescriptor(d.step, d.pitchClass)) }
        .toList
    }
  }

}

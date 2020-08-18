package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model._

import scala.reflect.ClassTag

object Parsers {

  implicit class TuningParseOps(tuning: TextTuning) {
    def parsePitchDescriptor: String => PitchDescriptor = input => parseDescriptor(
      tuning.textSharp,
      tuning.textFlat,
      tuning.textNotes,
      tuning.pitchClassSequence,
      input
    )

    def parseDegree: String => Degree = input => {
      val descriptor = parseDescriptor(
        tuning.textSharp,
        tuning.textFlat,
        tuning.textDegrees,
        tuning.pitchClassSequence,
        input
      )
      Degree(descriptor)
    }

    def parseInterval: String => PitchDescriptor = input => parseDescriptor(
        tuning.textSharp,
        tuning.textFlat,
        tuning.textIntervals,
        tuning.pitchClassSequence,
        input
      )

    def parseArray[A : ClassTag](input: String, extractor: String => A): Array[A] = {
      input.split(' ').map(s => extractor(s))
    }
  }

  def parseDescriptor(
    sharpSymbol: String,
    flatSymbol: String,
    symbolSequence: Seq[String],
    pitchClassSequence: Seq[Int],
    input: String
  ): PitchDescriptor = {
    val cleanInput = input.replace(sharpSymbol, "").replace(flatSymbol, "")
    val step = symbolSequence.indexOf(cleanInput)
    val pitchClass = pitchClassSequence(step)
    val sharps = input.count(_ == sharpSymbol.head)
    val flats = input.count(_ == flatSymbol.head)

    PitchDescriptor(step, pitchClass + sharps - flats)
  }

}

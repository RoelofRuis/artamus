package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model._

import scala.reflect.ClassTag

object Parsers {

  implicit class TuningParseOps(tuning: TextTuning) {
    def parsePitchDescriptor: String => PitchDescriptor = input =>
      parseDescriptor(tuning.textNotes, input)._1

    def parseDegree: String => Degree = input => {
      val (descriptor, res) = parseDescriptor(
        tuning.textDegrees,
        input
      )
      val quality = parseQuality(res)
      Degree(descriptor, quality)
    }

    def parseQuality: String => Quality = input => {
      tuning
        .qualities
        .find(_.symbol == input)
        .map { q =>
          val intervals = parseArray(q.intervals, parseInterval)
          Quality(intervals.toList)
        }
        .get
    }

    def parseInterval: String => PitchDescriptor = input =>
      parseDescriptor(tuning.textIntervals, input)._1

    def parseArray[A : ClassTag](input: String, extractor: String => A): Array[A] = {
      input.split(' ').map(s => extractor(s))
    }

    def parseDescriptor(
      symbolSequence: Seq[String],
      input: String
    ): (PitchDescriptor, String) = {
      val (sharps, res1) = count(input, tuning.textSharp)
      val (flats, res2) = count(res1, tuning.textFlat)
      val (stepSymbol, res3) = find(res2, symbolSequence)
      val step = symbolSequence.indexOf(stepSymbol)
      val pitchClass = tuning.pitchClassSequence(step)

      (PitchDescriptor(step, pitchClass + sharps - flats), res3)
    }
  }

  def count(input: String, target: String): (Int, String) = {
    if (input.startsWith(target)) {
      val result = count(input.stripPrefix(target), target)
      (result._1 + 1, result._2)
    }
    else (0, input)
  }

  def find(input: String, options: Seq[String]): (String, String) = {
    val found = options.map { opt => (input.startsWith(opt), opt) }
      .filter { case (startsWith, _) => startsWith }
      .maxBy { case (_, opt) => opt.length }
      ._2

    (found, input.stripPrefix(found))
  }

}

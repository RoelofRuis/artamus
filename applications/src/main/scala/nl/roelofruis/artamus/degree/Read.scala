package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model._

import scala.reflect.ClassTag

object Read {

  implicit class TuningReadOps(tuning: TextTuning) {
    def parsePitchDescriptor: String => PitchDescriptor = input => {
      val (stepSymbol, res1) = find(input, tuning.textNotes)
      val (sharps, res2) = count(res1, tuning.textSharp)
      val (flats, _) = count(res2, tuning.textFlat)
      val step = tuning.textNotes.indexOf(stepSymbol)
      val pitchClass = tuning.pitchClassSequence(step)
      PitchDescriptor(step, pitchClass - flats + sharps)
    }

    def parseDegree: String => Degree = input => {
      val (sharps, res1) = count(input, tuning.textSharp)
      val (flats, res2) = count(res1, tuning.textFlat)
      val (stepSymbol, res3) = find(res2, tuning.textDegrees)
      val quality = parseQuality(res3)
      val step = tuning.textDegrees.indexOf(stepSymbol)
      val pitchClass = tuning.pitchClassSequence(step)
      Degree(PitchDescriptor(step, pitchClass - flats + sharps), quality)
    }

    def parseChord: String => Chord = input => {
      val (stepSymbol, res1) = find(input, tuning.textNotes)
      val (sharps, res2) = count(res1, tuning.textSharp)
      val (flats, res3) = count(res2, tuning.textFlat)
      val step = tuning.textNotes.indexOf(stepSymbol)
      val pitchClass = tuning.pitchClassSequence(step)
      val quality = parseQuality(res3)
      Chord(PitchDescriptor(step, pitchClass - flats + sharps), quality)
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

    def parseInterval: String => PitchDescriptor = input => {
      val (sharps, res1) = count(input, tuning.textSharp)
      val (flats, res2) = count(res1, tuning.textFlat)
      val (stepSymbol, _) = find(res2, tuning.textIntervals)
      val step = tuning.textIntervals.indexOf(stepSymbol)
      val pitchClass = tuning.pitchClassSequence(step)
      PitchDescriptor(step, pitchClass - flats + sharps)
    }

    def parseArray[A: ClassTag](input: String, extractor: String => A): Array[A] = {
      input.split(' ').map(s => extractor(s))
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

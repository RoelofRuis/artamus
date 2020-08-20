package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model._
import nl.roelofruis.artamus.util.State

import scala.reflect.ClassTag

object Read {

  implicit class TuningReadOps(tuning: TextTuning) {
    def parsePitchDescriptor: State[String, PitchDescriptor] = {
      for {
        step <- find(tuning.textNotes)
        accidentals <- parseAccidentals
        pitchClass = tuning.pitchClassSequence(step)
      } yield PitchDescriptor(step, pitchClass + accidentals)
    }

    def parseDegree: State[String, Degree] = {
      for {
        accidentals <- parseAccidentals
        step <- find(tuning.textDegrees)
        quality <- parseQuality
        pitchClass = tuning.pitchClassSequence(step)
      } yield Degree(PitchDescriptor(step, pitchClass + accidentals), quality)
    }

    def parseChord: State[String, Chord] = {
      for {
        pitchDescriptor <- parsePitchDescriptor
        quality <- parseQuality
      } yield Chord(pitchDescriptor, quality)
    }

    def parseQuality: State[String, Quality] = State { s =>
      val textQuality = tuning.qualities.find(_.symbol == s).get
      val descriptors = parseArray(parseInterval).run(textQuality.intervals)._2.toList
      (s.stripPrefix(textQuality.symbol), Quality(descriptors))
    }

    def parseInterval: State[String, PitchDescriptor] = {
      for {
        accidentals <- parseAccidentals
        step <- find(tuning.textIntervals)
        pitchClass = tuning.pitchClassSequence(step)
      } yield PitchDescriptor(step, pitchClass + accidentals)
    }

    def parseAccidentals: State[String, Int] = {
      for {
        sharps <- count(tuning.textSharp)
        flats <- count(tuning.textFlat)
      } yield sharps - flats
    }

    def parseArray[A : ClassTag](extractor: State[String, A]): State[String, Array[A]] = State { s =>
      (s, s.split(' ').map(extractor.run).map(_._2))
    }
  }

  def count(target: String): State[String, Int] = State { s =>
    def loop(i: String): (String, Int) = {
      if (i.startsWith(target)) {
        val (res, n) = loop(i.stripPrefix(target))
        (res, n + 1)
      }
      else (i, 0)
    }

    loop(s)
  }

  def find(options: Seq[String]): State[String, Int] = State { s =>
    val found = options.map { opt => (s.startsWith(opt), opt) }
      .filter { case (startsWith, _) => startsWith }
      .maxBy { case (_, opt) => opt.length }
      ._2

    val index = options.indexOf(found)

    (s.stripPrefix(found), index)
  }

}

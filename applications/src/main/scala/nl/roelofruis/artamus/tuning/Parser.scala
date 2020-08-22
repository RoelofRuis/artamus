package nl.roelofruis.artamus.tuning

import nl.roelofruis.artamus.degree.Model._
import nl.roelofruis.artamus.util.State

import scala.annotation.tailrec
import scala.reflect.ClassTag

object Parser {

  trait MusicPrimitivesParser {
    val pitchClassSequence: List[Int]
    val textNotes: List[String]
    val textIntervals: List[String]
    val textSharp: String
    val textFlat: String

    def parsePitchDescriptor: State[String, PitchDescriptor] = {
      for {
        step <- find(textNotes)
        accidentals <- parseAccidentals
        pitchClass = pitchClassSequence(step)
      } yield PitchDescriptor(step, pitchClass + accidentals)
    }

    def parseInterval: State[String, PitchDescriptor] = {
      for {
        accidentals <- parseAccidentals
        step <- find(textIntervals)
        pitchClass = pitchClassSequence(step)
      } yield PitchDescriptor(step, pitchClass + accidentals)
    }

    def parseAccidentals: State[String, Int] = {
      for {
        sharps <- count(textSharp)
        flats <- count(textFlat)
      } yield sharps - flats
    }
  }

  trait MusicObjectsParser extends MusicPrimitivesParser {
    val textDegrees: List[String]
    val scaleMap: Map[String, Scale]
    val qualityMap: Map[String, Quality]

    def parseKey: State[String, Key] = {
      for {
        root <- parsePitchDescriptor
        _ <- strip(" ")
        scale <- parseScale
      } yield Key(root, scale)
    }

    def parseScale: State[String, Scale] = State { s =>
      val scale = scaleMap.get(s).get // TODO: No get!
      ("", scale)
    }

    def parseQuality: State[String, Quality] = State { s =>
      val quality = qualityMap.get(s).get // TODO: No get!
      ("", quality)
    }

    def parseDegree: State[String, Degree] = {
      for {
        accidentals <- parseAccidentals
        step <- find(textDegrees)
        quality <- parseQuality
        pitchClass = pitchClassSequence(step)
      } yield Degree(PitchDescriptor(step, pitchClass + accidentals), quality)
    }

    def parseChord: State[String, Chord] = {
      for {
        pitchDescriptor <- parsePitchDescriptor
        quality <- parseQuality
      } yield Chord(pitchDescriptor, quality)
    }
  }

  def parseArray[A : ClassTag](extractor: State[String, A]): State[String, Array[A]] = State { s =>
    (s, s.split(' ').map(extractor.run).map(_.value))
  }

  def strip(target: String): State[String, Unit] = State { s =>
    @tailrec def loop(i: String): String = {
      if (i.startsWith(target)) loop(i.stripPrefix(target))
      else i
    }
    (loop(s), ())
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
      .maxBy { case (_, opt) => opt.length } // TODO: Could be non existent!
      ._2

    val index = options.indexOf(found)

    (s.stripPrefix(found), index)
  }

}

package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.application.Model.{ParseResult, PitchedPrimitives}
import nl.roelofruis.artamus.core.track.Pitched.PitchDescriptor
import nl.roelofruis.artamus.core.track.Temporal.{Metre, PulseGroup}

import scala.annotation.tailrec
import scala.util.{Failure, Success}

/** @deprecated */
trait MusicPrimitivesParser {
  val symbols: PitchedPrimitives
  val buffer: ParseBuffer

  def parsePitchDescriptor: ParseResult[PitchDescriptor] = for {
    step <- buffer.findIndex(symbols.textNotes)
    accidentals <- parseAccidentals
    pitchClass = symbols.pitchClassSequence(step)
  } yield PitchDescriptor(step, pitchClass + accidentals)

  def parseDegreeDescriptor: ParseResult[PitchDescriptor] = for {
    accidentals <- parseAccidentals
    step <- buffer.findIndex(symbols.textDegrees)
    pitchClass = symbols.pitchClassSequence(step)
  } yield PitchDescriptor(step, pitchClass + accidentals)

  def parseInterval: ParseResult[PitchDescriptor] = for {
    accidentals <- parseAccidentals
    step <- buffer.findIndex(symbols.textIntervals)
    pitchClass = symbols.pitchClassSequence(step)
  } yield PitchDescriptor(step, pitchClass + accidentals)

  def parseAccidentals: ParseResult[Int] = for {
    sharps <- buffer.count(symbols.textSharp)
    flats <- buffer.count(symbols.textFlat)
  } yield sharps - flats

  def parseList[A](parse: => ParseResult[A], separator: String): ParseResult[List[A]] = {
    @tailrec def loop(res: List[A]): ParseResult[List[A]] = {
      if (buffer.isExhausted) Success(res)
      else {
        val parseResult = for {
          res <- parse
          _ <- buffer.skip(separator)
        } yield res
        parseResult match {
          case Success(a) => loop(res :+ a)
          case Failure(ex) => Failure(ex)
        }
      }
    }
    loop(List())
  }

  def parseMetre: ParseResult[Metre] = for {
    numPulses <- buffer.findIndex(Seq("1", "2", "3", "4", "5"))
    _ <- buffer.expectOne("/")
    pulseBase <- buffer.findIndex(Seq("1", "2", "4", "8")) // 1 / pow(2, n): The index produces the correct value of n
  } yield Metre(Seq(PulseGroup(pulseBase, numPulses + 1)))

}

object MusicPrimitivesParser {

  def apply(text: String, primitives: PitchedPrimitives): MusicPrimitivesParser = {
    new MusicPrimitivesParser {
      override val symbols: PitchedPrimitives = primitives
      override val buffer: ParseBuffer = ParseBuffer(text)
    }
  }

}


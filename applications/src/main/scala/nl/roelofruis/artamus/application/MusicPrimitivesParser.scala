package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.core.Pitched.PitchDescriptor
import nl.roelofruis.artamus.core.Temporal.{Metre, PulseGroup}
import nl.roelofruis.artamus.application.Model.{ParseResult, PitchedPrimitives}

import scala.annotation.tailrec
import scala.util.{Failure, Success}

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
          _ <- buffer.ignore(separator)
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
    num <- buffer.findIndex(Seq("1", "2", "3", "4", "5"))
    _ <- buffer.expectOne("/")
    denom <- buffer.findIndex(Seq("1", "2", "4", "8"))
  } yield Metre(Seq(PulseGroup(denom, num)))

}

object MusicPrimitivesParser {

  def apply(text: String, primitives: PitchedPrimitives): MusicPrimitivesParser = {
    new MusicPrimitivesParser {
      override val symbols: PitchedPrimitives = primitives
      override val buffer: ParseBuffer = ParseBuffer(text)
    }
  }

}


package nl.roelofruis.artamus.parsing

import nl.roelofruis.artamus.core.Pitched._
import nl.roelofruis.artamus.core.math.temporal.Window
import nl.roelofruis.artamus.parsing.Model.{ParseResult, PitchedObjects, PitchedPrimitives, Temporal}

trait MusicObjectsParser extends MusicPrimitivesParser {
  val symbols: PitchedPrimitives with PitchedObjects with Temporal
  val buffer: ParseBuffer

  def parseKey: ParseResult[Key] = for {
    root <- parsePitchDescriptor
    _ <- buffer.ignore(" ")
    scale <- parseScale
  } yield Key(root, scale)

  def parseScale: ParseResult[Scale] = for {
    scale <- buffer.find(symbols.scaleMap)
  } yield scale

  def parseQuality: ParseResult[Quality] = for {
    quality <- buffer.find(symbols.qualityMap)
  } yield quality

  def parseDegree: ParseResult[Degree] = {
    val degree = for {
      descriptor <- parseDegreeDescriptor
      quality <- parseQuality
      isTritoneSub = buffer.has("T")
    } yield Degree(descriptor, quality, None, isTritoneSub)

    if (buffer.has("/")) {
      for {
        deg <- degree
        relative <- parseDegreeDescriptor
      } yield deg.copy(relativeTo = Some(relative))
    } else degree
  }

  def parseChord: ParseResult[Chord] = for {
    pitchDescriptor <- parsePitchDescriptor
    quality <- parseQuality
  } yield Chord(pitchDescriptor, quality)

}

object MusicObjectsParser {

  def apply(text: String, primitives: PitchedPrimitives with PitchedObjects with Temporal): MusicObjectsParser = {
    new MusicObjectsParser {
      override val symbols: PitchedPrimitives with PitchedObjects with Temporal = primitives
      override val buffer: ParseBuffer = ParseBuffer(text)
    }
  }

}



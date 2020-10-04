package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.application.Model.{ParseResult, PitchedObjects, PitchedPrimitives}
import nl.roelofruis.artamus.core.track.Pitched._

trait MusicObjectsParser extends MusicPrimitivesParser {
  val symbols: PitchedPrimitives with PitchedObjects
  val buffer: ParseBuffer

  def parseKey: ParseResult[Key] = for {
    root <- parsePitchDescriptor
    _ <- buffer.skipSpaces
    scale <- parseScale
  } yield Key(root, scale)

  def parseScale: ParseResult[Scale] = for {
    scale <- buffer.find(symbols.scaleMap)
  } yield scale

  def parseQuality: ParseResult[Quality] = for {
    quality <- buffer.find(symbols.qualityMap)
  } yield quality

  def parseQualityGroup: ParseResult[QualityGroup] = for {
    qualityGroup <- buffer.find(symbols.qualityGroupMap)
  } yield qualityGroup

  def parseDegree: ParseResult[Degree] = {
    val degree = for {
      descriptor <- parseDegreeDescriptor
      quality <- parseQualityGroup
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

  def apply(text: String, primitives: PitchedPrimitives with PitchedObjects): MusicObjectsParser = {
    new MusicObjectsParser {
      override val symbols: PitchedPrimitives with PitchedObjects = primitives
      override val buffer: ParseBuffer = ParseBuffer(text)
    }
  }

}



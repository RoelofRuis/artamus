package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.application.Model.{ParseResult, PitchedPrimitives}
import nl.roelofruis.artamus.core.track.Pitched.PitchDescriptor

@deprecated("", "")
trait MusicPrimitivesParser {
  val symbols: PitchedPrimitives
  val buffer: ParseBuffer

  def parsePitchDescriptor: ParseResult[PitchDescriptor] = for {
    step <- buffer.findIndex(symbols.textNotes)
    accidentals <- parseAccidentals
    pitchClass = symbols.pitchClassSequence(step)
  } yield PitchDescriptor(step, pitchClass + accidentals)

  def parseAccidentals: ParseResult[Int] = for {
    sharps <- buffer.count(symbols.textSharp)
    flats <- buffer.count(symbols.textFlat)
  } yield sharps - flats

}

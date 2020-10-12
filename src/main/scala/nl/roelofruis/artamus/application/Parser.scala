package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.application.Model.{ParseResult, PitchedObjects, PitchedPrimitives, TemporalSettings}

import scala.util.Try

@deprecated("", "")
object Parser {

  implicit class Objects(symbols: PitchedPrimitives with PitchedObjects with TemporalSettings) {
    def parser(text: String): MusicObjectsParser = MusicObjectsParser(text, symbols)
  }

  implicit class ListOps[A](results: List[ParseResult[A]]) {
    def invert: ParseResult[List[A]] = {
      Try { results.map(_.get) }
    }
  }

}

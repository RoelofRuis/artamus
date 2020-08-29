package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.application.Model.{ParseResult, PitchedObjects, PitchedPrimitives, Temporal}

import scala.util.Try

object Parser {

  implicit class Primitives(symbols: PitchedPrimitives) {
    def parser(text: String): MusicPrimitivesParser = MusicPrimitivesParser(text, symbols)
  }

  implicit class Objects(symbols: PitchedPrimitives with PitchedObjects with Temporal) {
    def parser(text: String): MusicObjectsParser = MusicObjectsParser(text, symbols)
  }

  implicit class ListOps[A](results: List[ParseResult[A]]) {
    def invert: ParseResult[List[A]] = {
      Try { results.map(_.get) }
    }
  }

}

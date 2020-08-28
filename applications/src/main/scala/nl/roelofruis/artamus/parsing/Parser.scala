package nl.roelofruis.artamus.parsing

import nl.roelofruis.artamus.parsing.Model.{ParseResult, PitchedObjects, PitchedPrimitives}

import scala.util.Try

object Parser {

  implicit class SymbolOps(symbols: PitchedPrimitives with PitchedObjects) {
    def parser(text: String): MusicObjectsParser = MusicObjectsParser(text, symbols)
  }

  implicit class ListOps[A](results: List[ParseResult[A]]) {
    def invert: ParseResult[List[A]] = {
      Try { results.map(_.get) }
    }
  }

}

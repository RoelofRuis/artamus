package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.application.Model.ParseResult

import scala.util.Try

object Utils {

  implicit class ListOps[A](results: List[ParseResult[A]]) {
    def invert: ParseResult[List[A]] = {
      Try { results.map(_.get) }
    }
  }

}

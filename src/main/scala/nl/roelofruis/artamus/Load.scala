package nl.roelofruis.artamus

import nl.roelofruis.artamus.application.Application
import nl.roelofruis.artamus.application.Model.ParseResult
import nl.roelofruis.artamus.lilypond.Parser

import scala.util.Try

object Load extends App {

  import nl.roelofruis.artamus.application.Reader._

  def program: ParseResult[Unit] = for {
    (piece, _) <- readFile("src/main/resources/melody/{file}.ly")
    expr       <- Try { Parser.parseLilypond(piece).get.value }
    _          = println(expr)
  } yield ()

  Application.runRepeated(program)


}
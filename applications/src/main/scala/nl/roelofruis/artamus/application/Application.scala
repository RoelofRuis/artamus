package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.application.Model.{ParseError, ParseResult}

import scala.util.{Failure, Success}

object Application {

  def run(parseResult: => ParseResult[Any]): Unit = {
    case Success(()) =>
    case Failure(ParseError(message, input)) => println(s"$message in [$input]")
    case Failure(ex) => throw ex
  }

}

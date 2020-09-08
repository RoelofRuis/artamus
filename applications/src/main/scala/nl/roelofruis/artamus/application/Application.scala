package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.application.Model.{ParseError, ParseResult}

import scala.util.{Failure, Success}

object Application {

  def run(parseResult: => ParseResult[Any]): Unit = {
    parseResult match {
      case Success(_) =>
      case Failure(ParseError(message, input)) =>
        println(s"$message in [$input]")
      case Failure(ex) =>
        println("Program error")
        println(ex.getMessage)
        ex.printStackTrace()
    }
  }

}

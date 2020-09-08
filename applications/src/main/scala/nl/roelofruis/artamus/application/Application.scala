package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.application.Model.{ParseError, ParseResult}

import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.{Failure, Success}

object Application {

  @tailrec def maybeQuit(program: => ParseResult[Any]): ParseResult[Any] = {
    program match {
      case Success(_) =>
        StdIn.readLine("Quit?\n > ") match {
          case "q" => Success(())
          case _ => maybeQuit(program)
      }
      case x => x
    }
  }

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

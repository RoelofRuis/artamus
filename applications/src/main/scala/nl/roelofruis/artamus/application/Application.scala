package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.application.Model.{ParseError, ParseResult}

import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.{Failure, Success}

object Application {

  @tailrec def runRepeated(program: => ParseResult[Any]): Unit = {
    program match {
      case Success(_) =>
        StdIn.readLine("Quit?\n > ") match {
          case "q" | "y" => Success(())
          case _ => runRepeated(program)
      }
      case Failure(ParseError(message, input)) =>
        println(s"$message in [$input]")
      case Failure(ex) =>
        println("Program error")
        println(ex.getMessage)
        ex.printStackTrace()
    }
  }

}

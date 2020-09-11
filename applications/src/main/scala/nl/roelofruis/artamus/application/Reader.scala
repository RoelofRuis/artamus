package nl.roelofruis.artamus.application

import java.io.FileNotFoundException

import nl.roelofruis.artamus.application.Model.{ParseError, ParseResult, Settings}
import nl.roelofruis.artamus.core.track.Pitched.Degree

import scala.annotation.tailrec
import scala.io.{Source, StdIn}
import scala.util.{Failure, Try}

object Reader {

  implicit class Readers(tuning: Settings) {

    import nl.roelofruis.artamus.application.Parser._

    def readDegree: ParseResult[Degree] = retry {
      for {
        degree <- tuning.parser(StdIn.readLine("Degree\n > ")).parseDegree
      } yield degree
    }

  }

  def readFile(placeholderPath: String): ParseResult[(String, String)] = retry {
    val fileName = StdIn.readLine("Input file\n > ")
    val path = placeholderPath.replace("{file}", fileName)
    Try {
      val source = Source.fromFile(path)
      val contents = source.getLines().mkString(" ")
      source.close()
      (contents, fileName)
    }.recoverWith {
      case _: FileNotFoundException => Failure(ParseError(s"File cannot be found", path))
    }
  }

  @tailrec def retry[A](prog: => ParseResult[A]): Try[A] = {
    prog match {
      case Failure(ParseError(message, input)) =>
        println(s"$message in [$input]")
        retry(prog)
      case x => x
    }
  }

}

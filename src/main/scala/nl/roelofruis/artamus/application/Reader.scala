package nl.roelofruis.artamus.application

import java.io.FileNotFoundException

import nl.roelofruis.artamus.application.Model.{ParseError, ParseResult, Settings}
import nl.roelofruis.artamus.application.ObjectParsers._
import nl.roelofruis.artamus.core.track.Pitched.Degree

import scala.annotation.tailrec
import scala.io.{Source, StdIn}
import scala.util.{Failure, Try}

object Reader {

  implicit class Readers(tuning: Settings) {

    def readDegree: ParseResult[Degree] = retry {
      for {
        degree <- parse(StdIn.readLine("Degree\n > "), tuning.degree(_))
      } yield degree
    }

  }

  def readFile(placeholderPath: String, lineConcat: String = " "): ParseResult[(String, String)] = retry {
    val fileName = StdIn.readLine("Input file\n > ")
    val path = placeholderPath.replace("{file}", fileName)
    Try {
      val source = Source.fromFile(path)
      val contents = source.getLines().mkString(lineConcat)
      source.close()
      (contents, fileName)
    }.recoverWith {
      case _: FileNotFoundException => Failure(ParseError(s"File [$path] cannot be found"))
    }
  }

  @tailrec def retry[A](prog: => ParseResult[A]): Try[A] = {
    prog match {
      case Failure(ParseError(message)) =>
        println(message)
        retry(prog)
      case x => x
    }
  }

}

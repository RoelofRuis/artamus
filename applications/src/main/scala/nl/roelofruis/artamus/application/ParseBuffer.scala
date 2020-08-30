package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.application.Model.{ParseError, ParseResult}

import scala.util.{Failure, Success}

private[application] object ParseBuffer {

  def apply(text: String): ParseBuffer = new ParseBuffer(text, 0)

}

private [application] class ParseBuffer private (
  private val text: String,
  private var position: Int = 0
) {

  private def state: String = text.slice(position, text.length)
  private def advanceOver(text: String): Unit = position += text.length
  private def error[A](msg: String): ParseResult[A] = {
    var errorIndication = text.patch(position, "<", 0).patch(position + 2, ">", 0)
    if (position > 10) errorIndication = errorIndication.patch(0, "... ", position - 10)
    if (position + 12 < text.length) errorIndication = errorIndication.patch(position + 12, " ...", text.length)
    Failure(ParseError(msg, errorIndication))
  }

  def isExhausted: Boolean = position >= text.length

  def find[A](options: Map[String, A]): ParseResult[A] = {
    val longestMatch = options.keys
      .map(_.length)
      .maxOption
      .flatMap { maxValue =>
        Range.inclusive(maxValue, 1, -1)
          .map(state.take)
          .find(element => options.get(element).isDefined)
      }

    longestMatch match {
      case None => error(s"Unable to find on of [${options.keys.mkString(",")}]")
      case Some(value) =>
        advanceOver(value)
        Success(options(value))
    }
  }

  def findIndex(options: Seq[String]): ParseResult[Int] = {
    val longestMatch = options.map { opt => (state.startsWith(opt), opt) }
      .collect { case (true, opt) => opt }
      .maxByOption(_.length)

    longestMatch match {
      case None => error(s"Unable to find one of [${options.mkString(",")}]")
      case Some(value) =>
        advanceOver(value)
        Success(options.indexOf(value))
    }
  }

  def count(target: String): ParseResult[Int] = {
    var total = 0
    while (state.startsWith(target)) {
      advanceOver(target)
      total += 1
    }
    Success(total)
  }

  def has(target: String): Boolean = {
    if (state.startsWith(target)) {
      advanceOver(target)
      true
    } else false
  }

  def ignore(target: String): ParseResult[Unit] = {
    while (state.startsWith(target)) advanceOver(target)
    Success(())
  }

  def expectOne(target: String): ParseResult[Unit] = expectExactly(target, 1)
  def expectExactly(target: String, num: Int): ParseResult[Unit] = expect(target, Some(num), Some(num))
  def expect(target: String, atLeast: Option[Int] = None, atMost: Option[Int] = None): ParseResult[Unit] = {
    count(target) match {
      case Failure(ex) => Failure(ex)
      case Success(v) =>
        (atLeast, atMost) match {
          case (None, None) => Success(())
          case (None, Some(upper)) =>
            if (v <= upper) Success(()) else error(s"Expected at most $atMost [$target]")
          case (Some(lower), None) =>
            if (v >= lower) Success(()) else error(s"Expected at least $atLeast [$target]")
          case (Some(lower), Some(upper)) =>
            if (v >= lower && v <= upper) Success(()) else error(s"Expected between $atLeast and $atMost [$target]")
        }
    }
  }

}

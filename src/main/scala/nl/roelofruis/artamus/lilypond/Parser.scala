package nl.roelofruis.artamus.lilypond

import fastparse._
import SingleLineWhitespace._
import nl.roelofruis.artamus.lilypond.Grammar.{CompoundMusicExpression, EqualToPrevious, Note, PowerOfTwoWithDots}

object Parser extends App {

  def parseStep[_ : P] = P(("a" | "b" | "c" | "d" | "e" | "f" | "g").!.map {
    case "a" => 5
    case "b" => 6
    case "c" => 0
    case "d" => 1
    case "e" => 2
    case "f" => 3
    case "g" => 4
  })

  def parseFlat[_ : P] = P(("e".? ~ "s").rep(1).!.map(- _.count(_ == 's')))
  def parseSharp[_ : P] = P("is".rep(1).!.map(_.count(_ == 's')))
  def parseAccidental[_ : P] = P(parseFlat | parseSharp | "".!.map(_ => 0))

  def parseDurationPower[_ : P] = P(("1" | "2" | "4" | "8" | "16" | "32" | "64").!.map{
    case "1" => 0
    case "2" => 1
    case "4" => 2
    case "8" => 3
    case "16" => 4
    case "32" => 5
    case "64" => 6
  })
  def parseDurationDots[_ : P] = P(".".rep.!.map(_.length))

  def parseImplicitDuration[_ : P] = P("".!.map { _ => EqualToPrevious()})
  def parseExplicitDuration[_ : P] = P((parseDurationPower ~ parseDurationDots).map {
    case (pow, dots) => PowerOfTwoWithDots(pow, dots)
  })

  def parseDuration[_ : P] = P(parseExplicitDuration | parseImplicitDuration)

  def parseNote[_ : P] = P((parseStep ~ parseAccidental ~ parseDuration).map {
    case (step, accidentals, duration) => Note(step, accidentals, duration)
  })

  def parseMusic[_ : P] = P(parseNote.rep.map{ seq => CompoundMusicExpression(seq) })

  val res = parse("c8 d8 fis", parseMusic(_))

  println(res)

}
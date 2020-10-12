package nl.roelofruis.artamus.lilypond

import fastparse.MultiLineWhitespace._
import fastparse._
import nl.roelofruis.artamus.lilypond.Grammar._

object Parser extends App {

  def step[_ : P]: P[Int] = P(StringIn("a", "b", "c", "d", "e", "f", "g").!.map {
    case "a" => 5
    case "b" => 6
    case "c" => 0
    case "d" => 1
    case "e" => 2
    case "f" => 3
    case "g" => 4
  })

  def flats[_ : P]: P[Int] = P(("e".? ~ "s").rep(1).!.map(- _.count(_ == 's')))
  def sharps[_ : P]: P[Int] = P("is".rep(1).!.map(_.count(_ == 's')))
  def accidentals[_ : P]: P[Int] = P(flats | sharps | "".!.map(_ => 0))


  def octaveUp[_ : P]: P[Int] = P("'".rep(1).!.map(_.length))
  def octaveDown[_ : P]: P[Int] = P(",".rep(1).!.map(- _.length))
  def octave[_ : P]: P[Int] = P(octaveUp | octaveDown | "".!.map(_ => 0))

  def pitch[_ : P]: P[Pitch] = P((step ~ accidentals ~ octave).map {
    case (step, accidentals, octave) => Pitch(step, accidentals, octave + Constants.BASE_OCTAVE)
  })

  def durationDots[_ : P]: P[Int] = P(".".rep.!.map(_.length))
  def durationPower[_ : P]: P[Int] = P(StringIn("1", "2", "4", "8", "16", "32", "64").!.map{
    case "1" => 0
    case "2" => 1
    case "4" => 2
    case "8" => 3
    case "16" => 4
    case "32" => 5
    case "64" => 6
  })

  def implicitDuration[_ : P]: P[EqualToPrevious] = P("".!.map { _ => EqualToPrevious()})
  def explicitDuration[_ : P]: P[PowerOfTwoWithDots] = P((durationPower ~ durationDots).map {
    case (pow, dots) => PowerOfTwoWithDots(pow, dots)
  })

  def tie[_ : P]: P[Boolean] = P("~".?.!.map{_.length == 1})

  def duration[_ : P]: P[Duration] = P(explicitDuration | implicitDuration)

  def rest[_ : P]: P[Rest] = P("r" ~ duration.map(Rest))

  def note[_ : P]: P[Note] = P((pitch ~ duration ~ tie).map {
    case (pitch, duration, tie) => Note(pitch, duration, tie)
  })

  def barLineCheck[_ : P]: P[BarLineCheck] = P("|".!.map(_ => BarLineCheck()))

  def musicExpression[_ : P]: P[CME] = P((note | rest | barLineCheck).rep(1).map(CME))

  def anonymousScope[_: P]: P[CME] = P("{" ~ compoundMusicExpression ~ "}")
  def relativeScope[_ : P]: P[CME] = P(("\\relative" ~ pitch ~ "{" ~ compoundMusicExpression ~ "}").map {
    case (pitch, cme) => Relative(pitch, cme)
  })

  def compoundMusicExpression[_ : P]: P[CME] = P(
    (musicExpression | anonymousScope | relativeScope | comment).rep(1).map{ expressions =>
      val seq = expressions.foldLeft(Seq[ME]()) { case (acc, expr) =>
        expr match {
          case c: Comment => acc :+ c
          case expr: CME => acc ++ expr.contents
          case _ => acc
        }
      }
      CME(seq)
    }
  )

  def comment[_ : P]: P[Comment] = P("%" ~ CharsWhile(c => c != '\n' && c != '\r').!.map(Comment))

  def lilypond[_ : P]: P[LilypondDocument] = P((comment | compoundMusicExpression).rep ~ End)

  def parseLilypond(input: String): Parsed[LilypondDocument] = parse(input, lilypond(_))

}
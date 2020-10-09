package nl.roelofruis.artamus.lilypond

import fastparse.SingleLineWhitespace._
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
    case (step, accidentals, octave) => Pitch(step, accidentals, octave)
  })

  def durationPower[_ : P]: P[Int] = P(StringIn("1", "2", "4", "8", "16", "32", "64").!.map{
    case "1" => 0
    case "2" => 1
    case "4" => 2
    case "8" => 3
    case "16" => 4
    case "32" => 5
    case "64" => 6
  })
  def durationDots[_ : P]: P[Int] = P(".".rep.!.map(_.length))

  def implicitDuration[_ : P]: P[EqualToPrevious] = P("".!.map { _ => EqualToPrevious()})
  def explicitDuration[_ : P]: P[PowerOfTwoWithDots] = P((durationPower ~ durationDots).map {
    case (pow, dots) => PowerOfTwoWithDots(pow, dots)
  })

  def duration[_ : P]: P[Duration] = P(explicitDuration | implicitDuration)

  def rest[_ : P]: P[Rest] = P("r" ~ duration.map(Rest))

  def note[_ : P]: P[Note] = P((pitch ~ duration).map {
    case (pitch, duration) => Note(pitch, duration)
  })

  def musicExpression[_ : P]: P[CompoundMusicExpression] = P((note | rest).rep(1))

  def anonymousScope[_: P]: P[CompoundMusicExpression] = P("{" ~ compoundMusicExpression ~ "}")
  def relativeScope[_ : P]: P[CompoundMusicExpression] = P(("\\relative" ~ pitch ~ "{" ~ compoundMusicExpression ~ "}").map {
    case (pitch, cme) => Relative(pitch, cme)
  })

  def compoundMusicExpression[_ : P]: P[CompoundMusicExpression] = P(
    (musicExpression | anonymousScope | relativeScope).rep.map{ _.flatten }
  )

  def lilypond[_ : P]: P[CompoundMusicExpression] = P(compoundMusicExpression ~ End)

}
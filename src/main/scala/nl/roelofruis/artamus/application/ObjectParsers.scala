package nl.roelofruis.artamus.application

import fastparse._
import fastparse.SingleLineWhitespace._
import nl.roelofruis.artamus.application.Model.{PitchedObjects, PitchedPrimitives}
import nl.roelofruis.artamus.core.track.Pitched.{Chord, Degree, Key, PitchDescriptor, Quality, QualityGroup, Scale}
import nl.roelofruis.artamus.core.track.Temporal.{Metre, PulseGroup}

object ObjectParsers {

  private def oneOf[_: P](options: Seq[String]): P[String] = options.foldLeft(P[Unit](Fail))(_ | _).!
  private def fromMap[_ : P, A](map: Map[String, A])(s: String): P[A] = map.get(s).map(Pass).getOrElse(Fail)

  implicit class FromPitchedPrimitives(pp: PitchedPrimitives) {
    def step[_ : P](options: Seq[String]): P[Int] = P(oneOf(options).map{pp.textNotes.indexOf(_)})

    def flats[_ : P]: P[Int] = P(pp.textFlat.rep(1).!.map(- _.length))
    def sharps[_ : P]: P[Int] = P(pp.textSharp.rep(1).!.map(_.length))
    def accidentals[_ : P]: P[Int] = P(flats | sharps | "".!.map(_ => 0))

    def pitchDescriptor[_ : P]: P[PitchDescriptor] = P((step(pp.textNotes) ~ accidentals).map { case (step, accidentals) =>
      PitchDescriptor(step, pp.pitchClassSequence(step) + accidentals)
    })

    def degreeDescriptor[_ : P]: P[PitchDescriptor] = P((accidentals ~ step(pp.textDegrees)).map { case (accidentals, step) =>
      PitchDescriptor(step, pp.pitchClassSequence(step) + accidentals)
    })

    def interval[_ : P]: P[PitchDescriptor] = P((accidentals ~ step(pp.textDegrees)).map { case (accidentals, step) =>
      PitchDescriptor(step, pp.pitchClassSequence(step) + accidentals)
    })

    val powers = Seq("1", "2", "4", "8", "16", "32")
    def metre[_ : P]: P[Metre] = P((CharIn("0-9").rep(1).!.map(_.toInt) ~ "/" ~ oneOf(powers).map(powers.indexOf)).map { case (pulses, base) =>
      Metre(Seq(PulseGroup(base, pulses)))
    })
  }

  implicit class FromPitchedObjects(pp: PitchedPrimitives with PitchedObjects) {
    def scale[_ : P]: P[Scale] = P(CharIn("a-z").rep(1).!.flatMap(fromMap(pp.scaleMap)))

    def key[_ : P]: P[Key] = P((pp.pitchDescriptor ~ scale).map { case (pd, scale) => Key(pd, scale) })

    def quality[_ : P]: P[Quality] = P(CharIn("a-z").rep(1).!.flatMap(fromMap(pp.qualityMap)))

    def qualityGroup[_ : P]: P[QualityGroup] = P(CharIn("a-z").rep(1).!.flatMap(fromMap(pp.qualityGroupMap)))

    def chord[_ : P]: P[Chord] = P((pp.pitchDescriptor ~ quality).map { case (pd, quality) => Chord(pd, quality) })

    def degree[_ : P]: P[Degree] = P(
      (pp.degreeDescriptor ~ qualityGroup ~ "T".?.!.map(_.length == 1) ~ ("/" ~ pp.degreeDescriptor).?).map{
        case (root, quality, isTritoneSub, relativeTo) => Degree(root, quality, relativeTo, isTritoneSub)
      })
  }

}

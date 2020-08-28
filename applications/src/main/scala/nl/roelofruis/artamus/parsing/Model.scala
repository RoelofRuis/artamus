package nl.roelofruis.artamus.parsing

import nl.roelofruis.artamus.core.Model.{Quality, Scale}

import scala.util.Try

object Model {

  type ParseResult[A] = Try[A]

  final case class ParseError(message: String, input: String) extends Exception

  trait PitchedPrimitives {
    val pitchClassSequence: List[Int]
    val textDegrees: List[String]
    val textNotes: List[String]
    val textIntervals: List[String]
    val textSharp: String
    val textFlat: String
  }

  trait PitchedObjects {
    val scaleMap: Map[String, Scale]
    val qualityMap: Map[String, Quality]
  }

  trait TemporalPrimitives {
    val textBarLine: String
    val textBeatIndication: String
  }

}

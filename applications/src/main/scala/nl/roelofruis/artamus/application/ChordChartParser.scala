package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.application.Model.{ParseError, ParseResult, PitchedObjects, PitchedPrimitives, TemporalSettings}
import nl.roelofruis.artamus.application.Parser._
import nl.roelofruis.artamus.core.Pitched.Chord
import nl.roelofruis.artamus.core.analysis.TemporalMaths
import nl.roelofruis.artamus.core.primitives.Duration

import scala.util.{Failure, Success}

class ChordChartParser(
  tuning: PitchedPrimitives with PitchedObjects with TemporalSettings
) extends TemporalMaths {

  def parseChordChart(text: String): ParseResult[Seq[(Duration, Chord)]] = {
    val parser = tuning.parser(text)

    def parseBars(barList: Seq[Seq[Chord]]): ParseResult[Seq[Seq[Chord]]] = {
      if (parser.buffer.has(tuning.textBarLine)) {
        parser.buffer.ignore(" ")
        if (parser.buffer.isExhausted) Success(barList)
        else parseBars(barList :+ Seq())
      } else for {
        _ <- parser.buffer.ignore(" ")
        chord <- parser.parseChord
        _ <- parser.buffer.ignore(" ")
        bars <- parseBars(barList.dropRight(1) ++ Seq(barList.last :+ chord))
      } yield bars
    }

    parseBars(Seq(Seq())).flatMap { bars =>
      val chordsWithDuration = bars.map { bar =>
        tuning.defaultMetre
          .divide(bar.size)
          .map { dur => bar.map { chord => (dur, chord) }}
      }
      if ( ! chordsWithDuration.forall(_.isDefined)) Failure(ParseError("Unable to determine chord length", text))
      else Success(chordsWithDuration.collect { case Some(x) => x }.flatten)
    }
  }

}





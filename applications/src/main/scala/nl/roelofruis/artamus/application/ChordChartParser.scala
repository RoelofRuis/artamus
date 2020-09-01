package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.application.Model.{ParseError, ParseResult, PitchedObjects, PitchedPrimitives, TemporalSettings}
import nl.roelofruis.artamus.application.Parser._
import nl.roelofruis.artamus.core.Pitched.Chord
import nl.roelofruis.artamus.core.analysis.TemporalMaths
import nl.roelofruis.artamus.core.primitives.Duration

import scala.util.{Failure, Success}

case class ChordChartParser(
  tuning: PitchedPrimitives with PitchedObjects with TemporalSettings
) extends TemporalMaths {

  def parseChordChart(text: String): ParseResult[Seq[(Duration, Chord)]] = {
    val parser = tuning.parser(text)

    def parseBars(barList: Seq[Seq[Chord]]): ParseResult[Seq[Seq[Chord]]] = {
      if (parser.buffer.has(tuning.textBarLine)) {
        parser.buffer.skipSpaces
        if (parser.buffer.isExhausted) Success(barList)
        else parseBars(barList :+ Seq())
      } else if (parser.buffer.has(tuning.textRepeatMark)) {
        parser.buffer.skipSpaces
        val Seq(previousBar, bar) = barList.takeRight(2)
        if (bar.isEmpty) {
          // in new bar: repeat whole previous bar
          parseBars(barList.dropRight(1) :+ bar)
        } else {
          // in existing bar: repeat previous chord
          parseBars(barList.dropRight(1) :+ (bar :+ bar.last))
        }
      } else for {
        _ <- parser.buffer.skipSpaces
        chord <- parser.parseChord
        _ <- parser.buffer.skipSpaces
        bars <- parseBars(barList.dropRight(1) :+ (barList.last :+ chord))
      } yield bars
    }

    val chordsPerBar = parseBars(Seq(Seq())).flatMap { bars =>
      val chordsWithDuration = bars.map { bar =>
        tuning.defaultMetre
          .divide(bar.size)
          .map { dur => bar.map { chord => (dur, chord) }}
      }
      if ( ! chordsWithDuration.forall(_.isDefined)) Failure(ParseError("Unable to determine chord length", text))
      else Success(chordsWithDuration.collect { case Some(x) => x }.flatten)
    }

    chordsPerBar.map { chords =>
      chords.foldLeft(Seq[(Duration, Chord)]()) {
        case (Seq(), (duration, chord)) => Seq((duration, chord))
        case (acc, (duration, chord)) if (acc.last._2 == chord) =>
          acc.dropRight(1) :+ (Duration(acc.last._1.v + duration.v), acc.last._2)
        case (acc, (duration, chord)) => acc :+ (duration, chord)
      }
    }
  }

}





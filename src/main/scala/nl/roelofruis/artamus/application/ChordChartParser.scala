package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.application.Model._
import nl.roelofruis.artamus.application.Parser._
import nl.roelofruis.artamus.core.common.Containers.{Windowed, WindowedSeq}
import nl.roelofruis.artamus.core.common.Position
import nl.roelofruis.artamus.core.track.Layer.ChordSeq
import nl.roelofruis.artamus.core.track.Pitched.Chord
import nl.roelofruis.artamus.core.track.algorithms.TemporalMaths

import scala.util.{Failure, Success}

case class ChordChartParser(
  tuning: PitchedPrimitives with PitchedObjects with TemporalSettings with Defaults
) extends TemporalMaths {

  def parseChordChart(text: String): ParseResult[ChordSeq] = {
    val parser = tuning.parser(text)

    def parseBars(barList: Seq[Seq[Chord]]): ParseResult[Seq[Seq[Chord]]] = {
      if (parser.buffer.has(tuning.textBarLine)) {
        parser.buffer.skipSpaces
        if (parser.buffer.isExhausted) Success(barList) // All done
        else parseBars(barList :+ Seq()) // Insert next bar
      } else if (parser.buffer.has(tuning.textRepeatMark)) {
        parser.buffer.skipSpaces
        val Seq(previousBar, bar) = barList.takeRight(2)
        if (bar.isEmpty) {
          // in new bar: repeat whole previous bar
          parseBars(barList.dropRight(1) :+ previousBar)
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
      chords.foldLeft((Position.ZERO, WindowedSeq.empty[Chord])) {
        case ((pos, acc), (duration, chord)) if acc.nonEmpty && acc.last.element == chord =>
          val nextPos = pos + duration
          val nextSeq = acc.dropRight(1) :+ acc.last.copy(window=acc.last.window.stretchTo(nextPos))
          (nextPos, nextSeq)

        case ((pos, acc), (duration, chord)) =>
          val nextPos = pos + duration
          val nextSeq = acc :+ Windowed(pos, duration, chord)
          (nextPos, nextSeq)
      }
    }.map(_._2)
  }

}





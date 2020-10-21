package nl.roelofruis.artamus.application

import fastparse.MultiLineWhitespace._
import fastparse._
import nl.roelofruis.artamus.application.Model._
import nl.roelofruis.artamus.application.ObjectParsers._
import nl.roelofruis.artamus.core.common.Position
import nl.roelofruis.artamus.core.common.Temporal.{Timeline, Windowed}
import nl.roelofruis.artamus.core.track.Pitched.Chord
import nl.roelofruis.artamus.core.track.Track.ChordTimeline
import nl.roelofruis.artamus.core.track.algorithms.PitchedMaths.TuningDefinition
import nl.roelofruis.artamus.core.track.algorithms.TemporalMaths

import scala.util.{Failure, Success}

object ChordChartParsing extends TemporalMaths {

  private type ChordChart = Seq[ChartElement]

  private sealed trait ChartElement
  private final case class Barline() extends ChartElement
  private final case class ChartedChord(chord: Chord) extends ChartElement
  private final case class Repeat() extends ChartElement

  private implicit class Parser(tuning: TuningDefinition with PitchedObjects with Defaults) {

    def barline[_ : P]: P[Barline] = P("|").map(_ => Barline())
    def repeat[_ : P]: P[Repeat] = P("%").map(_ => Repeat())
    def chartedChord[_ : P]: P[ChartedChord] = tuning.chord.map(ChartedChord)

    def chordChart[_ : P]: P[ChordChart] = P((barline | repeat | chartedChord).rep)
  }

  implicit class ChordChartOps(tuning: TuningDefinition with PitchedObjects with Defaults) {
    def parseChordChart(text: String): ParseResult[ChordTimeline] = {
      doParse(text, tuning.chordChart(_))
        .map { chart =>
          chart.foldLeft(Seq(Seq[Chord]())) { case (bars, elem) =>
            val Seq(previousBar, bar) = if (bars.size > 1) bars.takeRight(2) else Seq(Seq[Chord]()) ++ bars.takeRight(1)
            elem match {
              case _: Barline => bars :+ Seq()
              case _: Repeat if bar.isEmpty => bars.dropRight(1) :+ previousBar
              case _: Repeat => bars.dropRight(1) :+ (bar :+ bar.last)
              case ChartedChord(chord) => bars.dropRight(1) :+ (bar :+ chord)
            }
          }
            .filter(_.nonEmpty)
        }
        .flatMap { bars =>
          val chordsWithDuration = bars.map { bar =>
            tuning.defaultMetre
              .divide(bar.size)
              .map { dur => bar.map { chord => (dur, chord) }}
          }
          if ( ! chordsWithDuration.forall(_.isDefined)) Failure(ParseError("Unable to determine chord length"))
          else Success(chordsWithDuration.collect { case Some(x) => x }.flatten)
        }
        .map { chords =>
          chords.foldLeft((Position.ZERO, Timeline.empty[Chord])) {
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
}

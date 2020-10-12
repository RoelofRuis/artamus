package nl.roelofruis.artamus.application

import fastparse._
import fastparse.MultiLineWhitespace._
import nl.roelofruis.artamus.application.Model._
import ObjectParsers._
import nl.roelofruis.artamus.core.common.Temporal.{Windowed, WindowedSeq}
import nl.roelofruis.artamus.core.common.Position
import nl.roelofruis.artamus.core.track.Layer.ChordSeq
import nl.roelofruis.artamus.core.track.Pitched.Chord
import nl.roelofruis.artamus.core.track.algorithms.TemporalMaths

import scala.util.{Failure, Success}

case class ChordChartParser(
  tuning: PitchedPrimitives with PitchedObjects with TemporalSettings with Defaults
) extends TemporalMaths {

  type ChordChart = Seq[ChartElement]

  sealed trait ChartElement
  final case class Barline() extends ChartElement
  final case class ChartedChord(chord: Chord) extends ChartElement
  final case class Repeat() extends ChartElement

  def barline[_ : P]: P[Barline] = P(tuning.textBarLine).map(_ => Barline())
  def repeat[_ : P]: P[Repeat] = P(tuning.textRepeatMark).map(_ => Repeat())
  def chord[_ : P]: P[ChartedChord] = tuning.chord.map(ChartedChord)

  def chordChart[_ : P]: P[ChordChart] = P((barline | repeat | chord).rep)

  def parseChordChart(text: String): ParseResult[ChordSeq] = {
    doParse(text, chordChart(_))
      .map { chart =>
        chart.foldLeft(Seq(Seq[Chord]())) { case (bars, elem) =>
          val Seq(previousBar, bar) = bars.takeRight(2)
          elem match {
            case _: Barline => bars :+ Seq()
            case _: Repeat if bar.isEmpty => bars.dropRight(1) :+ previousBar
            case _: Repeat => bars.dropRight(1) :+ (bar :+ bar.last)
            case ChartedChord(chord) => bars.dropRight(1) :+ (previousBar :+ chord)

          }
        }
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





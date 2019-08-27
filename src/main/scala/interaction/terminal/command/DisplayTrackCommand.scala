package interaction.terminal.command

import server.api.Commands.{GetTrack, TrackID}
import server.model.SymbolProperties.{MidiPitch, NoteDuration, NotePosition}
import server.model.Track
import server.util.Rational

import scala.util.Try

class DisplayTrackCommand extends Command {

  val name = "disp"
  val helpText = "Display symbolic track representation"
  override val argsHelp = Some("[id: Int]")

  def execute(bus: BusStub, args: Array[String]): CommandResponse = {
    val res: Try[CommandResponse] = for {
      id <- Try(TrackID(args(0).toLong))
      tpe <- Try(if (args.isDefinedAt(1)) args(1) else "")
    } yield {
      bus.execute(GetTrack(id))
        .fold(
          ex => display(s"Cannot display track [$id]: [$ex]"),
          symbolTrack => {
            tpe match {
              case "piano" => display(printPianoRoll(symbolTrack))
              case _ => display(printSymbolData(symbolTrack))
            }
          }
        )
    }

    returnRecovered(res)
  }

  private def printPianoRoll(symbolTrack: Track): String = {
    val indexedData: Map[Rational, Iterable[(Rational, Rational, Int)]] = symbolTrack.mapSymbols { symbol =>
      for {
        pos <- symbol.properties.collectFirst { case NotePosition(pos, nv) => nv * pos.toInt }
        dur <- symbol.properties.collectFirst { case NoteDuration(len, nv) => nv * len.toInt }
        pitch <- symbol.properties.collectFirst { case MidiPitch(pitch) => pitch }
      } yield (pos, dur, pitch)
    }
      .collect { case Some(x) => x }
      .groupBy { case (pos, _, _) => pos }

    val MIN_PITCH = 30
    val MAX_PITCH = 90
    val STEPS_PER_QUARTER = 8
    val GRID_STEP = Rational(1, STEPS_PER_QUARTER * 4)
    val MEASURES = 4

    var active = Map[Int, Rational]()

    Range.apply(0, STEPS_PER_QUARTER * 4 * MEASURES).map { i =>
      indexedData
        .getOrElse(GRID_STEP * i, Iterable())
        .map { case (_, dur, pitch) => (pitch, dur) }
        .foreach { case (pitch, dur) => active += (pitch -> dur)}

      val onQuarter = (i % STEPS_PER_QUARTER) == 0
      val sidebar = if (onQuarter) "-----+" else "     |"
      val activePitchString = Range.inclusive(MIN_PITCH, MAX_PITCH)
        .map { p =>
          if (active.get(p).isDefined) '#'
          else if (onQuarter) '-'
          else ' '
        }
        .mkString("")

      active = active.mapValues(_ - GRID_STEP).filterNot { case (_, dur) => dur <= Rational(0, 0) }

      s"$sidebar$activePitchString"
    }.mkString("\n")
  }

  private def printSymbolData(symbolTrack: Track): String = {
    symbolTrack.mapSymbols { symbol =>
      for {
        idx <- symbol.properties.collectFirst { case NotePosition(pos, _) => pos }
        posString <- symbol.properties.collectFirst { case NotePosition(pos, nv) => s"@ ${nv * pos.toInt}" }
        durString <- symbol.properties.collectFirst { case NoteDuration(len, nv) => s"for ${nv * len.toInt}" }
        pitchString <- symbol.properties.collectFirst { case MidiPitch(pitch) => s"MIDI($pitch)" }
      } yield {
        idx -> s"$pitchString $posString $durString"
      }
    }
      .collect {case Some(res) => res }
      .toArray
      .sortBy { case (pos, _) => pos }
      .map { case (_, string) => string }
      .mkString("\n")
  }

}

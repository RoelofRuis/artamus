package interaction.terminal.command

import application.api.CommandBus
import application.api.Commands.{GetTrack, TrackID}
import application.model.SymbolProperties.{MidiPitch, NoteDuration, NotePosition}
import application.model.Track

import scala.util.Try

class DisplayTrackCommand extends Command {

  val name = "disp"
  val helpText = "Display symbolic track representation"
  override val argsHelp = Some("[id: Int]")

  def execute(bus: CommandBus, args: Array[String]): CommandResponse = {
    val res: Try[CommandResponse] = for {
      id <- Try(TrackID(args(0).toLong))
    } yield {
      bus.execute(GetTrack(id))
        .fold(
          ex => display(s"Unable to transform to symbol track [$ex]"),
          symbolTrack => display(printSymbolTrack(symbolTrack))
        )
    }

    returnRecovered(res)
  }

  private def printSymbolTrack(symbolTrack: Track): String = {
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

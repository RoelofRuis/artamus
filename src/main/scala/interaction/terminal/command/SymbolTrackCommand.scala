package interaction.terminal.command

import application.api.CommandBus
import application.api.Commands.ToSymbolTrack
import application.model.event.MidiTrack
import application.model.event.domain.ID
import application.model.symbolic.SymbolProperties.{MidiPitch, NoteDuration, NotePosition}
import application.model.symbolic.Track

import scala.util.Try

class SymbolTrackCommand extends Command {

  val name = "symb"
  val helpText = "Transform a track to a symbolic representation"
  override val argsHelp = Some("[id: Int]")

  def execute(bus: CommandBus, args: Array[String]): CommandResponse = {
    val res: Try[CommandResponse] = for {
      id <- Try(ID[MidiTrack](args(0).toLong))
    } yield {
      bus.execute(ToSymbolTrack(id))
        .fold(
          ex => display(s"Unable to transform to symbol track [$ex]"),
          symbolTrack => display(printSymbolTrack(symbolTrack))
        )
    }

    returnRecovered(res)
  }

  private def printSymbolTrack(symbolTrack: Track): String = {
    symbolTrack.symbols.map { symbol =>
      for {
        idx <- symbol.properties.collectFirst { case NotePosition(pos, _) => pos }
        posString <- symbol.properties.collectFirst { case NotePosition(pos, nv) => s"@ ${nv * pos}" }
        durString <- symbol.properties.collectFirst { case NoteDuration(len, nv) => s"for ${nv * len}" }
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

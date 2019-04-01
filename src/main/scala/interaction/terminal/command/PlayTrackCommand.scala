package interaction.terminal.command

import application.api.Commands.Play
import application.api.CommandBus
import application.model.event.MidiTrack
import application.model.event.domain.ID

import scala.util.Try

class PlayTrackCommand extends Command {

  val name = "play"
  val helpText = "Play a track"
  override val argsHelp = Some("[id: Int]")

  def execute(bus: CommandBus, args: Array[String]): CommandResponse = {
    val res = for {
      id <- Try(ID[MidiTrack](args(0).toLong))
    } yield {
      if (bus.execute(Play(id)).isSuccess) continue
      else display(s"Unknown track ID [$id]")
    }

    returnRecovered(res)
  }
}

package interaction.terminal.command

import java.util.UUID

import application.api.CommandBus
import application.api.Commands.Play

import scala.util.Try

class PlayTrackCommand extends Command {

  val name = "play"
  val helpText = "Play a track"
  override val argsHelp = Some("[id: Int]")

  def execute(bus: CommandBus, args: Array[String]): CommandResponse = {
    val res = for {
      id <- Try(UUID.fromString(args(0)))
    } yield {
      if (bus.execute(Play(id)).isSuccess) continue
      else display(s"Unknown track ID [$id]")
    }

    returnRecovered(res)
  }
}

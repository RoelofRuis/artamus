package interaction.terminal.command

import application.command.TrackCommand.Play
import application.model._
import application.ports.MessageBus

import scala.util.Try

class PlayTrackCommand extends Command {

  val name = "play"
  val helpText = "Play a track"
  override val argsHelp = Some("[id: Int]")

  def execute(bus: MessageBus, args: Array[String]): CommandResponse = {
    val res = for {
      id <- Try(ID[Track](args(0).toLong))
    } yield {
      if (bus.execute(Play(id)).isSuccess) continue
      else display(s"Unknown track ID [$id]")
    }

    returnRecovered(res)
  }
}

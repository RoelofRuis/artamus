package interaction.terminal.command

import server.api.Commands.{Play, TrackID}

import scala.util.Try

class PlayTrackCommand extends Command {

  val name = "play"
  val helpText = "Play a track"
  override val argsHelp = Some("[id: Int]")

  def execute(bus: BusStub, args: Array[String]): CommandResponse = {
    val res = for {
      id <- Try(TrackID(args(0).toLong))
    } yield {
      if (bus.execute(Play(id)).isSuccess) continue
      else display(s"Unknown track ID [$id]")
    }

    returnRecovered(res)
  }
}

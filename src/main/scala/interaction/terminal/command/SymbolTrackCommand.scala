package interaction.terminal.command

import application.api.CommandBus
import application.api.Commands.ToSymbolTrack
import application.domain.{ID, Track}

import scala.util.Try

class SymbolTrackCommand extends Command {

  val name = "symb"
  val helpText = "Transform a track to a symbolic representation"
  override val argsHelp = Some("[id: Int]")

  def execute(bus: CommandBus, args: Array[String]): CommandResponse = {
    val res: Try[CommandResponse] = for {
      id <- Try(ID[Track](args(0).toLong))
    } yield {
      bus.execute(ToSymbolTrack(id))
        .fold(
          ex => display(s"Unable to quantize idea [$ex]"),
          symbolTrack => display(s"Symbol track: [$symbolTrack]")
        )
    }

    returnRecovered(res)
  }

}

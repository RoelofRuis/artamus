package interaction.terminal.command

import application.api.Commands.StoreRecorded
import application.api.CommandBus
import application.domain.{ID, Idea}

import scala.util.Try

class StoreRecordingCommand extends Command {

  val name = "store"
  val helpText = "Store the currently running recording"
  override val argsHelp = Some("[idea id: Int]")

  def execute(bus: CommandBus, args: Array[String]): CommandResponse = {
    val res = for {
      ideaId <- Try(ID[Idea](args(0).toLong))
      track <- bus.execute(StoreRecorded(ideaId))
    } yield track

    res.fold(
      ex => display(s"Could not store recording: $ex"),
      track => display(s"Stored ${track.id} with num events [${track.elements.size}]")
    )
  }
}

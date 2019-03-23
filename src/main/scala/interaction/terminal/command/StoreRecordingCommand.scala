package interaction.terminal.command

import application.api.Commands.StoreRecorded
import application.api.CommandBus
import application.domain.Track.Track_ID
import application.domain.{ID, Idea}

import scala.util.Try

class StoreRecordingCommand extends Command {

  val name = "store"
  val helpText = "Store the currently running recording"
  override val argsHelp = Some("[idea id: Int]")

  def execute(bus: CommandBus, args: Array[String]): CommandResponse = {
    val res: Try[(Track_ID, Int)] = for {
      ideaId <- Try(ID[Idea](args(0).toLong))
      (trackId, lenght) <- bus.execute(StoreRecorded(ideaId))
    } yield (trackId, lenght)

    res.fold(
      ex => display(s"Could not store recording: $ex"),
      data => display(s"Stored ${data._1} with num events [${data._2}]")
    )
  }
}

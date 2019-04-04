package interaction.terminal.command

import application.api.CommandBus
import application.api.Commands.{StoreRecorded, TrackID}

import scala.util.Try

class StoreRecordingCommand extends Command {

  val name = "store"
  val helpText = "Store the currently running recording"
  override val argsHelp = Some("[idea id: Int]")

  def execute(bus: CommandBus, args: Array[String]): CommandResponse = {
    val res: Try[(TrackID, Int)] = for {
      (trackId, lenght) <- bus.execute(StoreRecorded())
    } yield (trackId, lenght)

    res.fold(
      ex => display(s"Could not store recording: $ex"),
      data => display(s"Stored ${data._1} with num events [${data._2}]")
    )
  }
}

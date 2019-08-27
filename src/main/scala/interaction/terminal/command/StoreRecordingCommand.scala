package interaction.terminal.command

import server.api.Commands.{StoreRecorded, TrackID}

import scala.util.Try

class StoreRecordingCommand extends Command {

  val name = "store"
  val helpText = "Store the currently running recording"

  def execute(bus: BusStub, args: Array[String]): CommandResponse = {
    val res: Try[(TrackID, Int)] = for {
      (trackId, lenght) <- bus.execute(StoreRecorded())
    } yield (trackId, lenght)

    res.fold(
      ex => display(s"Could not store recording: $ex"),
      data => display(s"Stored ${data._1} with num events [${data._2}]")
    )
  }
}

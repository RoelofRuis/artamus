package old.terminal.command

import server.api.Commands.GetAll

class ListTracksCommand extends Command {

  val name = "list"
  val helpText = "List the current tracks"

  def execute(bus: BusStub, args: Array[String]): CommandResponse = {
    val res = for {
      trackData <- bus.execute(GetAll)
    } yield {
      trackData.map(trackId => s"[$trackId]").mkString("\n")
    }

    res.fold(
      err => display(s"Unable to list all tracks:[$err]"),
      text => display(text)
    )
  }

}

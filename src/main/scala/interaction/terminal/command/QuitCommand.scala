package interaction.terminal.command

import server.api.Actions.CloseApplication

class QuitCommand extends Command {

  val name = "quit"
  val helpText = "Exits the program."

  def execute(bus: BusStub, args: Array[String]): CommandResponse = {
    bus.execute(CloseApplication)
    halt
  }

}

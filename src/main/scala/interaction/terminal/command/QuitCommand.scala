package interaction.terminal.command

import server.api.Commands.Exit

class QuitCommand extends Command {

  val name = "quit"
  val helpText = "Exits the program."

  def execute(bus: BusStub, args: Array[String]): CommandResponse = {
    bus.execute(Exit)
    halt
  }

}

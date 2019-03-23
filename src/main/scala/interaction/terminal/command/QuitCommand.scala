package interaction.terminal.command

import application.api.Commands.CloseApplication
import application.api.CommandBus

class QuitCommand extends Command {

  val name = "quit"
  val helpText = "Exits the program."

  def execute(bus: CommandBus, args: Array[String]): CommandResponse = {
    bus.execute(CloseApplication)
    halt
  }

}

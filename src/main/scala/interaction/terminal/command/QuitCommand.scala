package interaction.terminal.command

import application.command.ApplicationCommand.CloseApplication
import application.ports.MessageBus

class QuitCommand extends Command {

  val name = "quit"
  val helpText = "Exits the program."

  def execute(bus: MessageBus, args: Array[String]): CommandResponse = {
    bus.execute(CloseApplication)
    halt
  }

}

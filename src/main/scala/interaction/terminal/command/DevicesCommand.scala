package interaction.terminal.command

import application.api.CommandBus
import application.api.Commands.GetDevices

class DevicesCommand extends Command {

  val name = "devices"
  val helpText = "View available (MIDI) devices"

  def execute(bus: CommandBus, args: Array[String]): CommandResponse = {
    bus.execute(GetDevices)
      .map(info => display(info.mkString("\n")))
      .getOrElse(display("Could not display device info"))
  }

}

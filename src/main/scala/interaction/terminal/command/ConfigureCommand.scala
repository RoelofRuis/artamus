package interaction.terminal.command

import application.controller.ServiceController
import interaction.terminal.Prompt
import interaction.terminal.command.ConfigureCommand.ConfigDescription
import javax.inject.Inject

class ConfigureCommand[A] @Inject() (
  controller: ServiceController[A],
  configDescription: ConfigDescription[A],
  prompt: Prompt
) extends Command {

  val name: String = configDescription.commandName
  val helpText: String = configDescription.commandHelpText

  def run(args: Array[String]): CommandResponse = {
    val info = controller.getAll.map {
      case (n, true) => s" (selected) > $n"
      case (n, false) => s"            - $n"
    }
      .mkString("", "\n","\n")
      .concat {
        if (controller.hasActive) "            - OFF"
        else " (selected) > OFF"
      }

    prompt.write(info)

    val selectedService = prompt.read(s"Which ${configDescription.serviceName} device to use?")

    if (selectedService.toLowerCase == "off") {
      controller.deactivateAll
      display(s"Using no ${configDescription.serviceName}")
    } else if (controller.activateOne(selectedService)) display(s"${configDescription.serviceName} [$selectedService] active")
    else display(s"Unknown ${configDescription.serviceName} [$selectedService]")
  }

}

object ConfigureCommand {

  case class ConfigDescription[A](serviceName: String, commandName: String, commandHelpText: String)

}
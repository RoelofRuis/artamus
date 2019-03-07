package interaction.terminal.command

import application.components.ServiceRegistry
import interaction.terminal.Prompt
import interaction.terminal.command.ConfigureCommand.ConfigDescription
import javax.inject.Inject

class ConfigureCommand[A] @Inject() (
  registry: ServiceRegistry[A],
  configDescription: ConfigDescription[A],
  prompt: Prompt
) extends Command {

  val name: String = configDescription.commandName
  val helpText: String = configDescription.commandHelpText

  def run(args: Array[String]): CommandResponse = {
    val info = registry.getRegistered.map { service =>
      if (registry.getActive.contains(service._1)) s" (selected) > ${service._1}"
      else s"            - ${service._1}"
    }
      .mkString("", "\n", "\n")
      .concat(registry.getActive.map(_ => "            - OFF").getOrElse(" (selected) > OFF"))

    prompt.write(info)

    val selectedService = prompt.read(s"Which ${configDescription.serviceName} device to use?")

    if (selectedService.toLowerCase == "off") {
      registry.deactivate()
      display(s"Using no ${configDescription.serviceName}")
    } else if (registry.setActive(selectedService)) display(s"${configDescription.serviceName} [$selectedService] active")
    else display(s"Unknown ${configDescription.serviceName} [$selectedService]")
  }

}

object ConfigureCommand {

  case class ConfigDescription[A](serviceName: String, commandName: String, commandHelpText: String)

}
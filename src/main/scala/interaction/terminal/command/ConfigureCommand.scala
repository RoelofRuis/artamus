package interaction.terminal.command

import core.application.ServiceRegistry
import interaction.terminal.Prompt
import interaction.terminal.command.ConfigureCommand.ConfigDescription
import javax.inject.Inject

class ConfigureCommand[A] @Inject() (
  registry: ServiceRegistry[A],
  configDescription: ConfigDescription[A],
  prompt: Prompt
) extends Command {

  val name: String = configDescription.commandName
  override val helpText: String = configDescription.commandHelpText

  def run(): CommandResponse = {
    val info = registry.getRegistered.map { service =>
      if (registry.getActive.contains(service._1)) s" (active) > ${service._1}"
      else s"          - ${service._1}"
    }.mkString("\n")

    prompt.write(info)

    val selectedService = prompt.read(s"Which ${configDescription.serviceName} device to use?")

    if (selectedService.isEmpty) display("Nothing changed")
    else if (registry.setActive(selectedService)) display(s"${configDescription.serviceName} [$selectedService] active")
    else display(s"Unknown ${configDescription.serviceName} [$selectedService]")
  }

}

object ConfigureCommand {

  case class ConfigDescription[A](serviceName: String, commandName: String, commandHelpText: String)

}
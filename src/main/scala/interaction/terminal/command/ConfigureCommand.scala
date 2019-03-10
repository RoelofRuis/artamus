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
    prompt.write(formatServiceInfo)

    val selectedService = prompt.read(s"Which ${configDescription.serviceName} to toggle?")

    if (selectedService.toLowerCase == "off") {
      controller.deactivateAll
      display(formatServiceInfo)
    }
    else if (controller.toggle(selectedService)) display(formatServiceInfo)
    else display(s"Unknown ${configDescription.serviceName} [$selectedService]")
  }

  private def formatServiceInfo: String = {
    val hasActive = if (controller.hasActive) s"[${controller.getAll.count { case (_, isActive) => isActive }}]" else "[OFF]"
    val allowsMultiple = if (controller.allowsMultiple) "[MULTIPLE]" else "[SINGLE]"

    val servicesStatus = controller.getAll.map {
      case (n, true) => s" (selected) > $n"
      case (n, false) => s"            - $n"
    }.mkString("\n")

    s"Service [${configDescription.serviceName}]: $allowsMultiple $hasActive\n$servicesStatus"
  }

}

object ConfigureCommand {

  case class ConfigDescription[A](serviceName: String, commandName: String, commandHelpText: String)

}
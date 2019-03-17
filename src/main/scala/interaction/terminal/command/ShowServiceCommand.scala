package interaction.terminal.command

import application.controller.ServiceController
import interaction.terminal.Prompt
import interaction.terminal.command.ConfigureServiceCommand.ConfigDescription
import javax.inject.Inject

class ShowServiceCommand[A] @Inject() (
  controller: ServiceController[A],
  configDescription: ConfigDescription[A]
) extends Command {

  val name: String = s"show-${configDescription.name}"
  val helpText: String = s"Show service info for ${configDescription.description}"

  def run(args: Array[String]): CommandResponse = {
    val hasActive = if (controller.hasActive) s"[${controller.getAll.count { case (_, isActive) => isActive }}]" else "[OFF]"
    val allowsMultiple = if (controller.allowsMultiple) "[MULTIPLE]" else "[SINGLE]"

    val servicesStatus = controller.getAll.map {
      case (n, true) => s" (selected) > $n"
      case (n, false) => s"            - $n"
    }.mkString("\n")

    display(s"Service [${configDescription.name}]: $allowsMultiple $hasActive\n$servicesStatus")

  }
}


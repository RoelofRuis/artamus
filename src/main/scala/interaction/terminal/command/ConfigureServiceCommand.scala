package interaction.terminal.command

import application.controller.ServiceController
import interaction.terminal.command.ConfigureServiceCommand.ConfigDescription
import javax.inject.Inject

import scala.util.Try

class ConfigureServiceCommand[A] @Inject() (
  controller: ServiceController[A],
  configDescription: ConfigDescription[A]
) extends Command {

  val name: String = s"conf-${configDescription.name}"
  val helpText: String = s"Configure ${configDescription.description}"
  override val argsHelp = Some("[service: String]")

  def run(args: Array[String]): CommandResponse = {
    val res = for {
      selectedService <- Try(args(0))
    } yield {
      if (selectedService.toLowerCase == "off") {
        controller.deactivateAll
        display("All deactivated")
      }
      else if (controller.toggle(selectedService)) display(s"toggled $selectedService")
      else display(s"Unknown ${configDescription.name} [$selectedService]")
    }

    res getOrElse display("Could not parse arguments")
  }
}

object ConfigureServiceCommand {

  case class ConfigDescription[A](name: String, description: String)

}
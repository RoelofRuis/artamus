package interaction.terminal.command

import core.application.ServiceRegistry
import core.components.InputDevice
import interaction.terminal.Prompt
import javax.inject.Inject

class SetInputDeviceCommand @Inject() (
  registry: ServiceRegistry[InputDevice],
  prompt: Prompt
) extends Command {

  val name = "set-input-device"
  override val helpText = "Set the used input device"

  def run(): CommandResponse = {
    val info = registry.getRegistered.map { service =>
      if (registry.getActive.contains(service._1)) s" > ${service._1}"
      else s" - ${service._1}"
    }.mkString("\n")

    prompt.write(info)

    val selectedLogger = prompt.read("Which input device to use?")

    if (registry.setActive(selectedLogger)) display(s"input device [$selectedLogger] active")
    else display(s"unknown input device [$selectedLogger]")
  }

}

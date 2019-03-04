package interaction.terminal.command

import core.application.ServiceRegistry
import core.components.InputDevice
import interaction.terminal.Prompt
import javax.inject.Inject

class SetInputDeviceCommand @Inject() (
  inputDeviceRegistry: ServiceRegistry[InputDevice],
  prompt: Prompt
) extends Command {

  val name = "set-input-device"
  override val helpText = "Set the used input device"

  def run(): CommandResponse = {
    val info = inputDeviceRegistry.getAvailableServices.map { inputDevice =>
      if (inputDevice._1 == inputDeviceRegistry.getActiveName) s" > ${inputDevice._1}"
      else s" - ${inputDevice._1}"
    }.mkString("\n")

    prompt.write(info)

    val selectedLogger = prompt.read("Which input device to use?")

    if (inputDeviceRegistry.makeActive(selectedLogger)) display(s"input device [$selectedLogger] active")
    else display(s"unknown input device [$selectedLogger]")
  }

}

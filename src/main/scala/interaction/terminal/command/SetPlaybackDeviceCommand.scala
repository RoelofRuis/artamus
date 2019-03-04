package interaction.terminal.command

import core.application.ServiceRegistry
import core.components.PlaybackDevice
import interaction.terminal.Prompt
import javax.inject.Inject

class SetPlaybackDeviceCommand @Inject() (
  registry: ServiceRegistry[PlaybackDevice],
  prompt: Prompt
) extends Command {

  val name = "set-playback-device"
  override val helpText = "Set the used playback device"

  def run(): CommandResponse = {
    val info = registry.getRegistered.map { service =>
      if (registry.getActive.contains(service._1)) s" > ${service._1}"
      else s" - ${service._1}"
    }.mkString("\n")

    prompt.write(info)

    val selectedLogger = prompt.read("Which playback device to use?")

    if (registry.setActive(selectedLogger)) display(s"playback device [$selectedLogger] active")
    else display(s"unknown playback device [$selectedLogger]")
  }

}

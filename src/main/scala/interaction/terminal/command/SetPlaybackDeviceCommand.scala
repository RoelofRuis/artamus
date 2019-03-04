package interaction.terminal.command

import core.application.ServiceRegistry
import core.components.PlaybackDevice
import interaction.terminal.Prompt
import javax.inject.Inject

class SetPlaybackDeviceCommand @Inject() (
  playbackDeviceCommand: ServiceRegistry[PlaybackDevice],
  prompt: Prompt
) extends Command {

  val name = "set-playback-device"
  override val helpText = "Set the used playback device"

  def run(): CommandResponse = {
    val info = playbackDeviceCommand.getAvailableServices.map { playbackDevice =>
      if (playbackDevice._1 == playbackDeviceCommand.getActiveName) s" > ${playbackDevice._1}"
      else s" - ${playbackDevice._1}"
    }.mkString("\n")

    prompt.write(info)

    val selectedLogger = prompt.read("Which playback device to use?")

    if (playbackDeviceCommand.makeActive(selectedLogger)) display(s"playback device [$selectedLogger] active")
    else display(s"unknown playback device [$selectedLogger]")
  }

}

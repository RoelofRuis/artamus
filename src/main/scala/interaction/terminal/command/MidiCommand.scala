package interaction.terminal.command

import com.google.inject.Inject
import interaction.midi.device.MidiInterface
import javax.sound.midi.MidiSystem

class MidiCommand @Inject() (interface: MidiInterface) extends Command {

  val name = "midi"
  override val helpText = "View available system MIDI resources"

  def run(): CommandResponse = {
    val info = MidiSystem.getMidiDeviceInfo
      .map(device => s"[${device.getName}][${device.getDescription}][${device.getVendor}][${device.getVersion}]")
      .mkString("\n")

    display(info)
  }

}

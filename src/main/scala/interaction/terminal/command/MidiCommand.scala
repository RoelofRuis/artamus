package interaction.terminal.command

import javax.sound.midi.MidiSystem

class MidiCommand extends Command {

  val name = "midi"
  override val helpText = "View available system MIDI resources"

  def run(): CommandResponse = {
    val info = MidiSystem.getMidiDeviceInfo
      .map(device => s"[${device.getName}][${device.getDescription}][${device.getVendor}][${device.getVersion}]")
      .mkString("\n")

    display(info)
  }

}

package interaction.terminal.command

import javax.sound.midi.MidiSystem

// TODO: remove this command or make it go via the core; MIDI logic should not be in here.
class MidiCommand extends Command {

  val name = "midi"
  val helpText = "View available system MIDI resources"

  def run(args: Array[String]): CommandResponse = {
    val info = MidiSystem.getMidiDeviceInfo
      .zipWithIndex
      .map { case (device, pos) =>
        s"$pos.\t${device.getName} (${device.getClass.getSimpleName})\n\t${device.getDescription} [${device.getVendor} - ${device.getVersion}]"
      }
      .mkString("\n")

    display(info)
  }

}

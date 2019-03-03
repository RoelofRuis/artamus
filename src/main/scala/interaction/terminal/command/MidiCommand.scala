package interaction.terminal.command

import com.google.inject.Inject
import interaction.terminal.Prompt
import javax.sound.midi.{MidiSystem, MidiUnavailableException, ShortMessage}

class MidiCommand @Inject() (prompt: Prompt) extends Command {

  val name = "midi"
  override val helpText = "Lists midi system resources"

  def run(): CommandResponse = {
    val maybeDevice = MidiSystem.getMidiDeviceInfo.find { device =>
      device.getName.contains("Focusrite USB MIDI") && device.getDescription.contains("External MIDI Port")
    }.map(MidiSystem.getMidiDevice)

    maybeDevice match {
      case Some(device) =>
        try {
          device.open()

          val receiver = device.getReceiver

          receiver.send(new ShortMessage(ShortMessage.NOTE_ON, 0, 60, 40), -1)

          receiver.close()
          device.close()
          continue
        } catch {
          case ex: MidiUnavailableException => display(s"Midi unavailable: $ex")
          case err: Throwable => display(s"Other exception: $err")
        }
      case None => display("Unable to open device")
    }
  }
}

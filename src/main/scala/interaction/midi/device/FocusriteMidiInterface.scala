package interaction.midi.device

import com.google.inject.Inject
import core.application.ResourceManager
import javax.sound.midi.{MidiDevice, MidiSystem}

// TODO: improve by catching Exceptions and using Options
class FocusriteMidiInterface @Inject() (resourceManager: ResourceManager) extends MidiInterface {

  private var midiInDevice: Option[MidiDevice] = None
  private var midiOutDevice: Option[MidiDevice] = None

  override def in: MidiDevice = {
    midiInDevice.getOrElse {
      val device: MidiDevice = MidiSystem.getMidiDeviceInfo.find { device =>
        device.getName.contains("Focusrite USB MIDI") && device.getDescription.contains("No details available")
      }.map(MidiSystem.getMidiDevice).get

      device.open()

      val name = "Focusrite USB MIDI IN"
      resourceManager.register(name, () => device.close())

      midiInDevice = Some(device)

      device
    }
  }

  override def out: MidiDevice = {
    midiOutDevice.getOrElse {
      val device: MidiDevice = MidiSystem.getMidiDeviceInfo.find { device =>
        device.getName.contains("Focusrite USB MIDI") && device.getDescription.contains("External MIDI Port")
      }.map(MidiSystem.getMidiDevice).get

      device.open()

      val name = "Focusrite USB MIDI OUT"
      resourceManager.register(name, () => device.close())

      midiOutDevice = Some(device)

      device
    }
  }

}

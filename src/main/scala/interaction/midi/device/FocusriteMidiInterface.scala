package interaction.midi.device

import com.google.inject.Inject
import core.application.{ResourceManager, ServiceRegistry}
import core.components.Logger
import javax.sound.midi.{MidiDevice, MidiSystem}

// TODO: improve by catching Exceptions and using Options
class FocusriteMidiInterface @Inject() (resourceManager: ResourceManager, logger: ServiceRegistry[Logger]) extends MidiInterface {

  private var midiInDevice: Option[MidiDevice] = None
  private var midiOutDevice: Option[MidiDevice] = None

  override def in: MidiDevice = {
    midiInDevice.getOrElse {
      val device: MidiDevice = MidiSystem.getMidiDeviceInfo.find { device =>
        device.getName.contains("Focusrite USB MIDI") && device.getDescription.contains("No details available")
      }.map(MidiSystem.getMidiDevice).get

      device.open()

      val name = "Focusrite USB MIDI IN"
      logger.getActive.debug(s"Opening [$name] [max recv ${device.getMaxReceivers}] [max tran ${device.getMaxTransmitters}]")
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
      logger.getActive.debug(s"Opening [$name] [max recv ${device.getMaxReceivers}] [max tran ${device.getMaxTransmitters}]")
      resourceManager.register(name, () => device.close())

      midiOutDevice = Some(device)

      device
    }
  }

}

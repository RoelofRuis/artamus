package interaction.midi.device

import com.google.inject.{Inject, Provider}
import core.application.ResourceManager
import javax.sound.midi.{MidiDevice, MidiSystem}

class FocusriteDeviceProvider @Inject() (resourceManager: ResourceManager) extends Provider[MidiDevice]{

  override val get: MidiDevice = {
    // TODO: fix using '.get' and make it safe!
    val device: MidiDevice = MidiSystem.getMidiDeviceInfo.find { device =>
      device.getName.contains("Focusrite USB MIDI") && device.getDescription.contains("External MIDI Port")
    }.map(MidiSystem.getMidiDevice).get

    device.open()

    resourceManager.register("Focusrite USB MIDI device", () => device.close())

    device
  }

}

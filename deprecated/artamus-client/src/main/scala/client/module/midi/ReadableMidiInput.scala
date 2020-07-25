package client.module.midi

import nl.roelofruis.midi.MidiResourceLoader
import nl.roelofruis.midi.read.MidiInput.ReadAction
import nl.roelofruis.midi.read.{AsyncReadableReceiver, MidiInput}
import javax.inject.{Inject, Named}
import javax.sound.midi.MidiMessage
import midi.{DeviceHash, MidiIO}
import nl.roelofruis.patching.PatchPanel

class ReadableMidiInput @Inject() (
  @Named("midi-in") deviceHash: DeviceHash,
  loader: MidiResourceLoader,
  patchPanel: PatchPanel
) extends MidiInput {

  import client.module.midi.MidiConnectors.canConnectMidi

  def readFrom(pick: List[MidiMessage] => ReadAction): MidiIO[List[MidiMessage]] = {
    val reader = new AsyncReadableReceiver
    for {
      inputDevice <- loader.loadDevice(deviceHash)
      transmitter <- MidiIO(inputDevice.getTransmitter)
      patchCableId <- MidiIO.wrap(patchPanel.connect(transmitter, reader, "ReadableMidiInput"))
    } yield {
      val result = reader.read(pick)
      patchPanel.disconnect(patchCableId)
      result
    }
  }

}

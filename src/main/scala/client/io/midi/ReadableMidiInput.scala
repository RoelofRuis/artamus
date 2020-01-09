package client.io.midi

import javax.inject.{Inject, Named}
import javax.sound.midi.MidiMessage
import midi.read.MidiInput.ReadAction
import midi.read.{AsyncReadableReceiver, MidiInput}
import midi.{DeviceHash, MidiIO, MidiResourceLoader}
import patching.PatchPanel

class ReadableMidiInput @Inject() (
  @Named("midi-in") deviceHash: DeviceHash,
  loader: MidiResourceLoader,
  patchPanel: PatchPanel
) extends MidiInput {

  import client.io.midi.MidiConnectors.canConnectMidi

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

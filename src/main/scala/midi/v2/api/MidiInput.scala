package midi.v2.api

import javax.sound.midi.MidiMessage
import midi.DeviceHash
import midi.v2.impl.{MidiSourceLoader, SingleDeviceMidiInput}

trait MidiInput {

  def readFrom(read: List[MidiMessage] => ReadAction): MidiIO[List[MidiMessage]]

}

object MidiInput {

  def apply(hash: DeviceHash, loader: MidiSourceLoader): MidiInput = new SingleDeviceMidiInput(hash, loader)

}



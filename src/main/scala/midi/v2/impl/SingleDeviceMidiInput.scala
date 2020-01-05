package midi.v2.impl

import javax.sound.midi.MidiMessage
import midi.DeviceHash
import midi.v2.api.{MidiIO, MidiInput, ReadAction}

final class SingleDeviceMidiInput(deviceHash: DeviceHash, loader: MidiSourceLoader) extends MidiInput {

  def readFrom(pick: List[MidiMessage] => ReadAction): MidiIO[List[MidiMessage]] = {
    val reader = new QueuedMidiReader
    for {
      source <- loader.loadSource(deviceHash)
    } yield {
      source.connect(reader)
      val result = reader.read(pick)
      source.disconnect(reader)
      result
    }
  }

}

package midi.in.impl

import javax.sound.midi.MidiMessage
import midi.in.api.MidiInput
import midi.{DeviceHash, MidiIO}

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

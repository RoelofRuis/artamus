import midi.in.{MidiMessageReader, QueuedMidiMessageReceiver}
import midi.out.{SequenceWriter, SimpleSequenceWriter}
import midi.resources.{MidiDeviceDescription, MidiResources}

package object midi {

  type DeviceHash = String

  // TODO: Use DI to get components, do not depend on globally available resource manager!
  final val midiResources: MidiResources = new MidiResources()

  def loadReader(deviceHash: DeviceHash): Option[MidiMessageReader] =
    MidiDeviceDescription.findDeviceInfo(deviceHash) match {
      case None => println(s"Unknown device hash! [$deviceHash]"); None
      case Some(info) => midiResources.loadTransmitter(info).map(new QueuedMidiMessageReceiver(_))
    }

  def loadSequenceWriter(deviceHash: DeviceHash, resolution: Int): Option[SequenceWriter] = {
    MidiDeviceDescription.findDeviceInfo(deviceHash) match {
      case None => println(s"Unknown device hash! [$deviceHash]"); None
      case Some(info) =>
        for {
          receiver <- midiResources.loadReceiver(info)
          sequencer <- midiResources.loadSequencer
        } yield new SimpleSequenceWriter(receiver, sequencer, resolution)
    }
  }

}

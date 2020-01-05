import midi.out.{SequenceWriter, SequenceWriterImpl}
import midi.resources.{MidiDeviceDescription, MidiResources}

package object midi {

  type DeviceHash = String

  // TODO: Use DI to get components, do not depend on globally available resource manager!
  final val midiResources: MidiResources = new MidiResources()

  @deprecated
  def loadSequenceWriter(deviceHash: DeviceHash): Option[SequenceWriter] = {
    MidiDeviceDescription.findDeviceInfo(deviceHash) match {
      case None => println(s"Unknown device hash! [$deviceHash]"); None
      case Some(info) =>
        for {
          receiver <- midiResources.loadReceiver(info)
          sequencer <- midiResources.loadSequencer
        } yield new SequenceWriterImpl(receiver, sequencer)
    }
  }

}

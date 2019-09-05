package client

import client.components.MusicWriter
import client.midi.in.ReadMidiMessage
import client.midi.util.{BlockingQueueReader, BlockingReadList}
import javax.inject.Inject
import javax.sound.midi.{MidiMessage, ShortMessage}
import music.{Key, TimeSignature}
import protocol.client.ClientInterface
import server.control.Disconnect

class Bootstrapper @Inject() (
  client: ClientInterface,
  reader: BlockingQueueReader[MidiMessage]
) {

  def run(): Unit = {

    val writer = new MusicWriter(client)

    writer.writeTimeSignature(TimeSignature.`4/4`)
    writer.writeKey(Key.`C-Major`)

    reader.read(ReadMidiMessage.noteOn(4))
      .map { case msg: ShortMessage => msg.getData1 }
      .foreach { writer.writeQuarterNote }

    reader.read(BlockingReadList.untilEnter)

    client.sendControl(Disconnect(false))
    client.close()

    midi.resourceManager.closeAll()


  }

}

package client.operations

import client.midi.in.ReadMidiMessage
import client.midi.util.BlockingQueueReader
import com.google.inject.Inject
import javax.sound.midi.{MidiMessage, ShortMessage}

class DevOperations @Inject() (
  registry: OperationRegistry,
  reader: BlockingQueueReader[MidiMessage]
) {

  registry.registerOperation("print-midi", () => {
    reader.read(ReadMidiMessage.noteOn(1))
      .foreach { case msg: ShortMessage =>
        print(s"COMMAND:${msg.getCommand}\nCHANNEL:${msg.getChannel}\nDATA1:${msg.getData1}\nDATA2:${msg.getData2}\n")
      }
    List()
  })

}

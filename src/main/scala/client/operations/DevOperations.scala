package client.operations

import midi.in.MidiMessageReader
import com.google.inject.Inject
import javax.sound.midi.ShortMessage

class DevOperations @Inject() (
  registry: OperationRegistry,
  reader: MidiMessageReader
) {

  import midi.in.Reading._

  registry.registerOperation("print-midi", () => {
    reader.noteOn(1)
      .foreach { case msg: ShortMessage =>
        print(s"COMMAND:${msg.getCommand}\nCHANNEL:${msg.getChannel}\nDATA1:${msg.getData1}\nDATA2:${msg.getData2}\n")
      }
    List()
  })

}

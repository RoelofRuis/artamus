package client.operations

import client.midi.in.ReadMidiMessage
import client.midi.util.BlockingQueueReader
import javax.inject.Inject
import javax.sound.midi.{MidiMessage, ShortMessage}
import music._
import protocol.client.ClientInterface
import server.control.Disconnect
import server.domain.track.{AddNote, SetKey, SetTimeSignature}

case object Quit extends Operation {
  override def getControl: List[protocol.Control] = List(Disconnect(true))
  override def getCommands: List[protocol.Command] = List()
}

class ClientOperationRegistry @Inject() (
  client: ClientInterface,
  reader: BlockingQueueReader[MidiMessage]
) extends OperationRegistry {

  override def getOperation(token: String): Option[Operation] = token match {
    case "quit" => Some(Quit)

    case "ts" => Some(new Operation {
      override def getControl: List[protocol.Control] = List()
      override def getCommands: List[protocol.Command] = {
        List(SetTimeSignature(TimeSignature.`4/4`))
      }
    })

    case "key" => Some(new Operation {
      override def getControl: List[protocol.Control] = List()
      override def getCommands: List[protocol.Command] = {
        List(SetKey(Key.`C-Major`))
      }
    })

    case "nuts" => Some(new Operation {
      override def getControl: List[protocol.Control] = List()
      override def getCommands: List[protocol.Command] = {
        reader.read(ReadMidiMessage.noteOn(4))
          .map {
            case msg: ShortMessage =>
              AddNote(
                Position.apply(Duration.QUARTER, 0),
                Note(Duration.QUARTER, MidiPitch.fromMidiPitchNumber(msg.getData1))
              )
          }
      }
    })

    case _ => None
  }

}

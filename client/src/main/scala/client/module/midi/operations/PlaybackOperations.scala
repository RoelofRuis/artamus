package client.module.midi.operations

import api.Write.Perform
import client.module.Operations.OperationRegistry
import client.module.midi.MusicWriter
import client.util.ClientLogging
import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import midi.write.MidiSequenceWriter
import protocol.client.api.ClientInterface

class PlaybackOperations @Inject() (
  registry: OperationRegistry,
  midiOutput: MidiSequenceWriter,
  client: ClientInterface
) extends LazyLogging {

  import ClientLogging._
  import MusicWriter._

  registry.local("play", "query (terminal)", {
    client.sendQueryLogged(Perform) match {
      case Right(track) => midiOutput.play(track)  match {
        case Left(ex) => logger.warn("Error while playing MIDI sequence", ex)
        case _ =>
      }
      case _ =>
    }
  })

}

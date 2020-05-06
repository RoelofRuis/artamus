package client.module.midi.operations

import client.infra.{Client, ClientInteraction}
import client.module.Operations.OperationRegistry
import client.module.midi.MusicWriter
import com.typesafe.scalalogging.LazyLogging
import artamus.core.api.Perform.PreparePerformance
import javax.inject.Inject
import nl.roelofruis.midi.write.MidiSequenceWriter

class PlaybackOperations @Inject() (
  registry: OperationRegistry,
  midiOutput: MidiSequenceWriter,
  client: Client
) extends LazyLogging {

  import ClientInteraction._
  import MusicWriter._

  registry.local("play", "query (terminal)", {
    client.sendQuery(PreparePerformance) match {
      case Right(track) => midiOutput.play(track)  match {
        case Left(ex) => logger.warn("Error while playing MIDI sequence", ex)
        case _ =>
      }
      case _ =>
    }
  })

}

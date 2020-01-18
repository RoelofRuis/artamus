package client.io.midi

import client.ClientLogging
import client.operations.Operations.OperationRegistry
import com.google.inject.Inject
import protocol.client.api.ClientInterface
import server.actions.recording.GetCurrentRecording

class MidiDebugOperations @Inject() (
  registry: OperationRegistry,
  client: ClientInterface
) {

  import ClientLogging._

  registry.local("showrec", "midi-debug", {
    client.sendQueryLogged(GetCurrentRecording) match {
      case Right(rec) =>
        rec.notes.foreach { note =>
          println(s"${note.starts} : ${note.noteNumber} : ${note.loudness}")
        }
      case _ =>
    }
  })

}

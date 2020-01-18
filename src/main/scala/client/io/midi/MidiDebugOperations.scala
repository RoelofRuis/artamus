package client.io.midi

import client.operations.Operations.OperationRegistry
import com.google.inject.Inject
import protocol.client.api.ClientInterface
import server.actions.recording.GetCurrentRecording

class MidiDebugOperations @Inject() (
  registry: OperationRegistry,
  client: ClientInterface
) {

  registry.local("showrec", "midi-debug", {
    client.sendQuery(GetCurrentRecording) match {
      case Left(ex) => println(ex)
      case Right(rec) =>
        rec.notes.foreach { note =>
          println(s"${note.starts} : ${note.noteNumber} : ${note.loudness}")
        }
    }
  })

}

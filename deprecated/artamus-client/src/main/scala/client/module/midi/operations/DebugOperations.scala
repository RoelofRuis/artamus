package client.module.midi.operations

import client.infra.{Client, ClientInteraction}
import client.module.Operations.OperationRegistry
import com.google.inject.Inject
import artamus.core.api.Record.GetCurrentRecording

class DebugOperations @Inject() (
  registry: OperationRegistry,
  client: Client
) {

  import ClientInteraction._

  registry.local("showrec", "midi-debug", {
    client.sendQuery(GetCurrentRecording) match {
      case Right(rec) =>
        rec.notes.foreach { note =>
          println(s"${note.starts} : ${note.noteNumber} : ${note.loudness}")
        }
      case _ =>
    }
  })

}

package client.operations

import client.write.QuarterNotesFormat
import javax.inject.Inject
import midi.out.SequenceWriter
import protocol.ClientInterface
import server.domain.track.{GetChords, GetMeta, GetMidiPitches, GetNotes}

class TrackQueryOperations @Inject() (
  registry: OperationRegistry,
  client: ClientInterface,
  midiOut: SequenceWriter
){

  registry.registerOperation(OperationToken("view-meta", "track-query"), () => {
    val optionSymbols = client.sendQuery(GetMeta)
    optionSymbols.foreach { syms =>
      syms.sortBy(_.id).foreach { sym =>
        println(s"[${sym.id}] [$sym]")
      }
    }
    List()
  })

  registry.registerOperation(OperationToken("view-notes", "track-query"), () => {
    val optionSymbols = client.sendQuery(GetNotes)
    optionSymbols.foreach { syms =>
      syms.sortBy(_.id).foreach { sym =>
        println(s"[${sym.id}] [$sym]")
      }
    }
    List()
  })

  registry.registerOperation(OperationToken("view-chords", "track-query"), () => {
    val optionSymbols = client.sendQuery(GetChords)
    optionSymbols.foreach { syms =>
      syms.sortBy(_.id).foreach { sym =>
        println(s"[${sym.id}] [$sym]")
      }
    }
    List()
  })

  registry.registerOperation(OperationToken("play", "track-query"), () => {
    val notes = client.sendQuery(GetMidiPitches)
    notes.foreach { notes =>
      midiOut.writeFromFormat(QuarterNotesFormat(notes))
    }
    List()
  })

}

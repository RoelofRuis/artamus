package client.operations

import client.MusicPlayer
import javax.inject.Inject
import protocol.ClientInterface
import server.domain.track.{ReadChords, ReadMidiNotes, ReadNotes}

class TrackQueryOperations @Inject() (
  registry: OperationRegistry,
  client: ClientInterface,
  musicPlayer: MusicPlayer
){

  registry.registerOperation(OperationToken("view-notes", "track-query"), () => {
    val optionSymbols = client.sendQuery(ReadNotes)
    optionSymbols.foreach { syms =>
      syms.sortBy(_.id).foreach { sym =>
        println(s"[${sym.id}] [$sym]")
      }
    }
    List()
  })

  registry.registerOperation(OperationToken("view-chords", "track-query"), () => {
    val optionSymbols = client.sendQuery(ReadChords)
    optionSymbols.foreach { syms =>
      syms.sortBy(_.id).foreach { sym =>
        println(s"[${sym.id}] [$sym]")
      }
    }
    List()
  })

  registry.registerOperation(OperationToken("play", "track-query"), () => {
    client.sendQuery(ReadMidiNotes) match {
      case None =>
      case Some(notes) => musicPlayer.play(notes)
    }
    List()
  })

}

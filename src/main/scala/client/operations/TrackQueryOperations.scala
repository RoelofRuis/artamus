package client.operations

import client.MusicPlayer
import javax.inject.Inject
import protocol.ClientInterface
import server.domain.track.{GetChords, GetMidiPitches, GetNotes}

class TrackQueryOperations @Inject() (
  registry: OperationRegistry,
  client: ClientInterface,
  musicPlayer: MusicPlayer
){

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
      musicPlayer.play(notes)
    }
    List()
  })

}

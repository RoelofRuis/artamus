package client.operations

import client.MusicPlayer
import javax.inject.Inject
import protocol.ClientInterface
import server.domain.track.{ReadChords, Perform, ReadNotes}

class TrackQueryOperations @Inject() (
  registry: OperationRegistry,
  client: ClientInterface,
  musicPlayer: MusicPlayer
){

  registry.registerOperation(OperationToken("view-notes", "track-query"), () => {
    val optionSymbols = client.sendQuery(ReadNotes)
    optionSymbols.foreach { notes =>
      notes.foreach { note => println(s"[$note]") }
    }
    List()
  })

  registry.registerOperation(OperationToken("view-chords", "track-query"), () => {
    val optionSymbols = client.sendQuery(ReadChords)
    optionSymbols.foreach { chords =>
      chords
        .sortBy { case (window, _) => window.start }
        .foreach { case (window, chord) => println(s"[$window]:[$chord]")}
    }
    List()
  })

  registry.registerOperation(OperationToken("play", "track-query"), () => {
    client.sendQuery(Perform) match {
      case None =>
      case Some(track) => musicPlayer.play(track)
    }
    List()
  })

}

package client.operations

import client.MusicPlayer
import javax.inject.Inject
import protocol.ClientInterface
import server.domain.writing.Perform

class TrackQueryOperations @Inject() (
  registry: OperationRegistry,
  client: ClientInterface,
  musicPlayer: MusicPlayer
){

  registry.registerOperation(OperationToken("play", "track-query"), () => {
    client.sendQuery(Perform) match {
      case None =>
      case Some(track) => musicPlayer.play(track)
    }
    List()
  })

}

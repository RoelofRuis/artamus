package client.operations

import client.MusicPlayer
import javax.inject.Inject
import protocol.client.api.ClientInterface2
import server.actions.writing.Perform

class TrackQueryOperations @Inject() (
  registry: OperationRegistry,
  client: ClientInterface2,
  musicPlayer: MusicPlayer
){

  registry.registerOperation(OperationToken("play", "track-query"), () => {
    client.sendQuery(Perform) match {
      case Left(ex) => println(ex) // TODO: better error handling
      case Right(track) => musicPlayer.play(track)
    }
    List()
  })

}

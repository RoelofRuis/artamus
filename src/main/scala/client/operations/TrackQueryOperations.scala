package client.operations

import client.MusicPlayer
import client.operations.Operations.OperationRegistry
import javax.inject.Inject
import protocol.client.api.ClientInterface
import server.actions.writing.Perform

class TrackQueryOperations @Inject() (
  registry: OperationRegistry,
  client: ClientInterface,
  musicPlayer: MusicPlayer
){

  registry.local("play", "track-query", {
    client.sendQuery(Perform) match {
      case Left(ex) => println(ex) // TODO: better error handling
      case Right(track) => musicPlayer.play(track)
    }
  })

}

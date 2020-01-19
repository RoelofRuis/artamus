package client.operations

import client.{ClientLogging, MusicPlayer}
import client.operations.Operations.OperationRegistry
import javax.inject.Inject
import protocol.client.api.ClientInterface
import server.actions.writing.Perform

class TrackQueryOperations @Inject() (
  registry: OperationRegistry,
  client: ClientInterface,
  musicPlayer: MusicPlayer
){

  import ClientLogging._

  registry.local("play", "track-query", {
    client.sendQueryLogged(Perform) match {
      case Right(track) => musicPlayer.play(track)
      case _ =>
    }
  })

}

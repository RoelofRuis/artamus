package server.infra

import java.util.UUID

import api.Write.TrackRendered
import domain.workspace.User
import domain.workspace.User.UserId
import javax.inject.Inject
import protocol.Event
import pubsub.EventBus
import storage.api.Database

final class ConnectionLifetimeHooks @Inject() (
  db: Database,
  eventbus: EventBus[Event],
) {

  import server.model.Renders._
  import server.model.Users._
  import server.model.Workspaces._

  def onServerStarted(): Unit = {
    val transaction = db.newTransaction
    transaction.saveUser(User(UserId(UUID.fromString("00000000-0000-0000-0000-000000000000")), "artamus"))
    transaction.commit()
  }

  def onAuthenticated(user: User): Unit = {
    for {
      workspace <- db.getWorkspaceByOwner(user)
      render <- db.getRenderByTrackId(workspace.selectedTrack)
    } yield {
      eventbus.publish(TrackRendered(render))
    }
  }

}

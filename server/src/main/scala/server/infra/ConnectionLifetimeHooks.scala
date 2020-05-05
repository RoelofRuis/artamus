package server.infra

import java.util.UUID

import artamus.core.api.Display.TrackRendered
import artamus.core.model.workspace.User
import artamus.core.model.workspace.User.UserId
import javax.inject.Inject
import server.api.ServerEventBus
import storage.api.Database

final class ConnectionLifetimeHooks @Inject() (
  db: Database,
  eventbus: ServerEventBus,
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
      render <- db.getRenderByTrackId(workspace.editingTrack)
    } yield {
      eventbus.publish(TrackRendered(render))
    }
  }

}

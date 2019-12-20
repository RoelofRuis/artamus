package server.infra

import javax.inject.Inject
import music.model.write.user.User
import protocol.Event
import pubsub.EventBus
import server.actions.writing.TrackRendered
import storage.api.{DbIO, DbTransaction}

final class ConnectionLifetimeHooks @Inject() (
  eventbus: EventBus[Event],
) {

  import server.model.Renders._
  import server.model.Workspaces._

  def onAuthenticated(db: DbIO with DbTransaction, user: User): Unit = {
    for {
      workspace <- db.getWorkspaceByOwner(user)
      render <- db.getRenderByTrackId(workspace.selectedTrack)
    } yield {
      eventbus.publish(TrackRendered(render))
    }
  }

}

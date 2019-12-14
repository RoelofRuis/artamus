package server.control

import javax.inject.Inject
import music.domain.user.User
import protocol.Event
import pubsub.EventBus
import server.domain.track.TrackRendered
import server.storage.api.{DbIO, DbTransaction}

final class ConnectionLifetimeHooks @Inject() (
  eventbus: EventBus[Event],
) {

  import server.storage.entity.Workspaces._
  import server.storage.entity.Renders._

  def onAuthenticated(db: DbIO with DbTransaction, user: User): Unit = {
    for {
      workspace <- db.getWorkspaceByOwner(user)
      render <- db.getRenderByTrackId(workspace.editedTrack)
    } yield {
      eventbus.publish(TrackRendered(render))
    }
  }

}

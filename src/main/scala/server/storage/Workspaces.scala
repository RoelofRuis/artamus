package server.storage

import music.domain.track.Track.TrackId
import music.domain.user.User
import music.domain.user.User.UserId
import music.domain.workspace.Workspace
import server.entity.EntityResult
import server.storage.api.{DataKey, DbIO}
import storage.api.DbRead

object Workspaces {

  import storage.EntityIO._

  private val KEY = DataKey("workspace")

  object WorkspaceJsonProtocol extends DomainProtocol {
    final case class WorkspaceModel(userId: UserId, trackId: TrackId)
    final case class WorkspaceMapModel(workspaces: Map[String, WorkspaceModel] = Map())

    implicit val workspaceFormat = jsonFormat2(WorkspaceModel)
    implicit val workspaceMapFormat = jsonFormat1(WorkspaceMapModel)
  }

  import WorkspaceJsonProtocol._

  implicit class WorkspaceQueries(db: DbRead) {
    def getWorkspaceByOwner(user: User): EntityResult[Workspace] = {
      db.readModel[WorkspaceMapModel](KEY).flatMap {
        _.workspaces.get(user.id.id.toString) match {
          case None => EntityResult.notFound
          case Some(w) =>
            EntityResult.found(Workspace(
              w.userId,
              w.trackId
            ))
        }
      }
    }
  }

  implicit class WorkspaceCommands(db: DbIO) {
    def saveWorkspace(workspace: Workspace): EntityResult[Unit] = {
      db.updateModel[WorkspaceMapModel](
        KEY,
        WorkspaceMapModel(),
        model => WorkspaceMapModel(
          model.workspaces.updated(
            workspace.owner.id.toString,
            WorkspaceModel(
              workspace.owner,
              workspace.editedTrack
            )
          )
        )
      )
    }
  }

}
